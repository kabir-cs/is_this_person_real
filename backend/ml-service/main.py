from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn
import io
import base64
from PIL import Image
import numpy as np
import cv2
import hashlib
import time
from typing import Dict, Any
import logging

from ml_engine import MLEngine
from config import settings

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="AI Face Detection ML Service",
    description="Machine learning service for detecting AI-generated faces",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize ML engine
ml_engine = MLEngine()

@app.on_event("startup")
async def startup_event():
    """Initialize the ML model on startup"""
    logger.info("Starting AI Face Detection ML Service...")
    ml_engine.load_model()
    logger.info("ML model loaded successfully")

@app.get("/")
async def root():
    """Health check endpoint"""
    return {
        "message": "AI Face Detection ML Service",
        "version": "1.0.0",
        "status": "running"
    }

@app.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "model_loaded": ml_engine.is_model_loaded(),
        "model_version": ml_engine.get_model_version()
    }

@app.post("/analyze")
async def analyze_image(file: UploadFile = File(...)):
    """
    Analyze an uploaded image for AI-generated face detection
    
    Args:
        file: Image file (JPEG, PNG, GIF)
    
    Returns:
        Analysis result with detection label, confidence, and scores
    """
    try:
        # Validate file type
        if not file.content_type.startswith('image/'):
            raise HTTPException(status_code=400, detail="File must be an image")
        
        # Read and process image
        image_data = await file.read()
        image = Image.open(io.BytesIO(image_data))
        
        # Convert to RGB if necessary
        if image.mode != 'RGB':
            image = image.convert('RGB')
        
        # Convert to numpy array
        image_array = np.array(image)
        
        # Calculate image hash
        image_hash = hashlib.sha256(image_data).hexdigest()
        
        # Start timing
        start_time = time.time()
        
        # Perform analysis
        result = ml_engine.analyze_image(image_array)
        
        # Calculate processing time
        processing_time = (time.time() - start_time) * 1000  # Convert to milliseconds
        
        # Prepare response
        response = {
            "label": result["label"],
            "confidence": result["confidence"],
            "scores": result["scores"],
            "model_version": ml_engine.get_model_version(),
            "processing_time_ms": processing_time,
            "image_hash": image_hash,
            "file_size": len(image_data),
            "mime_type": file.content_type
        }
        
        logger.info(f"Analysis completed for {file.filename}: {result['label']} ({result['confidence']:.2f})")
        
        return JSONResponse(content=response)
        
    except Exception as e:
        logger.error(f"Error analyzing image: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")

@app.post("/analyze_batch")
async def analyze_batch(files: list[UploadFile] = File(...)):
    """
    Analyze multiple images in batch
    
    Args:
        files: List of image files
    
    Returns:
        List of analysis results
    """
    try:
        results = []
        
        for file in files:
            try:
                # Validate file type
                if not file.content_type.startswith('image/'):
                    results.append({
                        "filename": file.filename,
                        "error": "File must be an image"
                    })
                    continue
                
                # Read and process image
                image_data = await file.read()
                image = Image.open(io.BytesIO(image_data))
                
                if image.mode != 'RGB':
                    image = image.convert('RGB')
                
                image_array = np.array(image)
                
                # Perform analysis
                result = ml_engine.analyze_image(image_array)
                
                results.append({
                    "filename": file.filename,
                    "label": result["label"],
                    "confidence": result["confidence"],
                    "scores": result["scores"],
                    "success": True
                })
                
            except Exception as e:
                results.append({
                    "filename": file.filename,
                    "error": str(e),
                    "success": False
                })
        
        return JSONResponse(content={"results": results})
        
    except Exception as e:
        logger.error(f"Error in batch analysis: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Batch analysis failed: {str(e)}")

@app.get("/model_info")
async def get_model_info():
    """Get information about the loaded ML model"""
    return {
        "model_version": ml_engine.get_model_version(),
        "model_type": ml_engine.get_model_type(),
        "input_shape": ml_engine.get_input_shape(),
        "classes": ml_engine.get_classes(),
        "loaded": ml_engine.is_model_loaded()
    }

@app.post("/retrain")
async def retrain_model():
    """Trigger model retraining (admin only)"""
    # This would typically require authentication
    try:
        # Placeholder for retraining logic
        return {"message": "Model retraining initiated", "status": "started"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Retraining failed: {str(e)}")

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    ) 