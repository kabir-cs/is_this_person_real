import numpy as np
import cv2
import face_recognition
from deepface import DeepFace
import tensorflow as tf
from transformers import pipeline
import logging
from typing import Dict, Any, List, Tuple
import os
import json

logger = logging.getLogger(__name__)

class MLEngine:
    """
    Machine Learning Engine for AI-generated face detection
    """
    
    def __init__(self):
        self.model = None
        self.model_version = "1.0.0"
        self.model_type = "ensemble"
        self.input_shape = (224, 224, 3)
        self.classes = ["REAL", "AI_GENERATED"]
        self.model_loaded = False
        
        # Initialize face detection models
        self.face_detector = None
        self.deepface_models = ["VGG-Face", "Facenet", "Facenet512", "OpenFace", "DeepID", "ArcFace"]
        
        # Initialize image classification pipeline
        self.classifier = None
        
    def load_model(self):
        """Load the ML model and initialize components"""
        try:
            logger.info("Loading ML model components...")
            
            # Initialize face detection
            self.face_detector = cv2.CascadeClassifier(
                cv2.data.haarcascades + 'haarcascade_frontalface_default.xml'
            )
            
            # Initialize image classification pipeline
            # Using a pre-trained model for image classification
            self.classifier = pipeline(
                "image-classification",
                model="microsoft/resnet-50",
                device=-1  # Use CPU
            )
            
            self.model_loaded = True
            logger.info("ML model loaded successfully")
            
        except Exception as e:
            logger.error(f"Error loading ML model: {str(e)}")
            self.model_loaded = False
            raise
    
    def analyze_image(self, image: np.ndarray) -> Dict[str, Any]:
        """
        Analyze an image for AI-generated face detection
        
        Args:
            image: Input image as numpy array (RGB)
            
        Returns:
            Dictionary containing analysis results
        """
        if not self.model_loaded:
            raise RuntimeError("ML model not loaded")
        
        try:
            # Preprocess image
            processed_image = self.preprocess_image(image)
            
            # Perform multiple analysis methods
            results = {}
            
            # 1. Face detection and analysis
            face_results = self.analyze_faces(image)
            results.update(face_results)
            
            # 2. Image quality analysis
            quality_results = self.analyze_image_quality(image)
            results.update(quality_results)
            
            # 3. Deep learning classification
            dl_results = self.deep_learning_analysis(processed_image)
            results.update(dl_results)
            
            # 4. Metadata analysis
            metadata_results = self.analyze_metadata(image)
            results.update(metadata_results)
            
            # Combine results and make final prediction
            final_result = self.combine_results(results)
            
            return final_result
            
        except Exception as e:
            logger.error(f"Error in image analysis: {str(e)}")
            return {
                "label": "UNCERTAIN",
                "confidence": 0.0,
                "scores": {"REAL": 0.5, "AI_GENERATED": 0.5},
                "error": str(e)
            }
    
    def preprocess_image(self, image: np.ndarray) -> np.ndarray:
        """Preprocess image for analysis"""
        # Resize to standard size
        resized = cv2.resize(image, (224, 224))
        
        # Normalize pixel values
        normalized = resized.astype(np.float32) / 255.0
        
        return normalized
    
    def analyze_faces(self, image: np.ndarray) -> Dict[str, Any]:
        """Analyze faces in the image using multiple methods"""
        results = {}
        
        try:
            # Convert to grayscale for face detection
            gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
            
            # Detect faces
            faces = self.face_detector.detectMultiScale(
                gray, 
                scaleFactor=1.1, 
                minNeighbors=5,
                minSize=(30, 30)
            )
            
            if len(faces) == 0:
                results["face_detected"] = False
                results["face_count"] = 0
                results["face_analysis"] = "No faces detected"
                return results
            
            results["face_detected"] = True
            results["face_count"] = len(faces)
            
            # Analyze each face
            face_scores = []
            for (x, y, w, h) in faces:
                face_img = image[y:y+h, x:x+w]
                
                # Face encoding analysis
                try:
                    encodings = face_recognition.face_encodings(face_img)
                    if encodings:
                        # Analyze face encoding quality
                        encoding = encodings[0]
                        encoding_variance = np.var(encoding)
                        face_scores.append(encoding_variance)
                except Exception as e:
                    logger.warning(f"Face encoding failed: {str(e)}")
            
            # Calculate face analysis metrics
            if face_scores:
                avg_face_score = np.mean(face_scores)
                results["face_encoding_variance"] = avg_face_score
                results["face_analysis"] = "Faces analyzed successfully"
            else:
                results["face_encoding_variance"] = 0.0
                results["face_analysis"] = "Face encoding failed"
                
        except Exception as e:
            logger.error(f"Face analysis error: {str(e)}")
            results["face_analysis"] = f"Error: {str(e)}"
        
        return results
    
    def analyze_image_quality(self, image: np.ndarray) -> Dict[str, Any]:
        """Analyze image quality metrics"""
        results = {}
        
        try:
            # Convert to grayscale for analysis
            gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
            
            # Calculate various quality metrics
            results["brightness"] = np.mean(gray)
            results["contrast"] = np.std(gray)
            results["sharpness"] = self.calculate_sharpness(gray)
            results["noise_level"] = self.estimate_noise(gray)
            
            # Color analysis
            results["color_variance"] = np.var(image, axis=(0, 1))
            results["saturation"] = self.calculate_saturation(image)
            
        except Exception as e:
            logger.error(f"Image quality analysis error: {str(e)}")
            results["quality_analysis"] = f"Error: {str(e)}"
        
        return results
    
    def deep_learning_analysis(self, image: np.ndarray) -> Dict[str, Any]:
        """Perform deep learning analysis"""
        results = {}
        
        try:
            # Convert numpy array to PIL Image for the pipeline
            from PIL import Image
            pil_image = Image.fromarray((image * 255).astype(np.uint8))
            
            # Get classification results
            predictions = self.classifier(pil_image)
            
            # Extract relevant predictions
            for pred in predictions:
                label = pred["label"].lower()
                score = pred["score"]
                
                if "person" in label or "face" in label or "human" in label:
                    results["human_detection_score"] = score
                elif "artificial" in label or "fake" in label:
                    results["ai_detection_score"] = score
            
            # Default scores if not found
            if "human_detection_score" not in results:
                results["human_detection_score"] = 0.5
            if "ai_detection_score" not in results:
                results["ai_detection_score"] = 0.5
                
        except Exception as e:
            logger.error(f"Deep learning analysis error: {str(e)}")
            results["dl_analysis"] = f"Error: {str(e)}"
            results["human_detection_score"] = 0.5
            results["ai_detection_score"] = 0.5
        
        return results
    
    def analyze_metadata(self, image: np.ndarray) -> Dict[str, Any]:
        """Analyze image metadata and patterns"""
        results = {}
        
        try:
            # Analyze pixel patterns
            results["pixel_uniformity"] = self.calculate_pixel_uniformity(image)
            results["edge_density"] = self.calculate_edge_density(image)
            results["texture_complexity"] = self.calculate_texture_complexity(image)
            
            # Check for common AI generation artifacts
            results["artifacts_detected"] = self.detect_artifacts(image)
            
        except Exception as e:
            logger.error(f"Metadata analysis error: {str(e)}")
            results["metadata_analysis"] = f"Error: {str(e)}"
        
        return results
    
    def combine_results(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """Combine all analysis results into final prediction"""
        try:
            # Initialize scores
            real_score = 0.0
            ai_score = 0.0
            
            # Weight different analysis components
            weights = {
                "face_analysis": 0.3,
                "quality_analysis": 0.2,
                "deep_learning": 0.3,
                "metadata": 0.2
            }
            
            # Face analysis scoring
            if results.get("face_detected", False):
                face_variance = results.get("face_encoding_variance", 0.0)
                # Higher variance might indicate AI generation
                if face_variance > 0.1:
                    ai_score += weights["face_analysis"] * 0.7
                    real_score += weights["face_analysis"] * 0.3
                else:
                    real_score += weights["face_analysis"] * 0.7
                    ai_score += weights["face_analysis"] * 0.3
            else:
                # No face detected - uncertain
                real_score += weights["face_analysis"] * 0.5
                ai_score += weights["face_analysis"] * 0.5
            
            # Quality analysis scoring
            sharpness = results.get("sharpness", 0.0)
            noise = results.get("noise_level", 0.0)
            
            if sharpness > 50 and noise < 10:
                # High quality, likely real
                real_score += weights["quality_analysis"] * 0.8
                ai_score += weights["quality_analysis"] * 0.2
            elif sharpness < 20 or noise > 30:
                # Low quality, might be AI
                ai_score += weights["quality_analysis"] * 0.6
                real_score += weights["quality_analysis"] * 0.4
            else:
                real_score += weights["quality_analysis"] * 0.5
                ai_score += weights["quality_analysis"] * 0.5
            
            # Deep learning scoring
            human_score = results.get("human_detection_score", 0.5)
            ai_detection = results.get("ai_detection_score", 0.5)
            
            real_score += weights["deep_learning"] * human_score
            ai_score += weights["deep_learning"] * ai_detection
            
            # Metadata scoring
            artifacts = results.get("artifacts_detected", False)
            if artifacts:
                ai_score += weights["metadata"] * 0.8
                real_score += weights["metadata"] * 0.2
            else:
                real_score += weights["metadata"] * 0.6
                ai_score += weights["metadata"] * 0.4
            
            # Normalize scores
            total = real_score + ai_score
            if total > 0:
                real_score /= total
                ai_score /= total
            
            # Determine final label
            if real_score > ai_score:
                label = "REAL"
                confidence = real_score
            else:
                label = "AI_GENERATED"
                confidence = ai_score
            
            # Ensure minimum confidence threshold
            if confidence < 0.6:
                label = "UNCERTAIN"
                confidence = 0.5
            
            return {
                "label": label,
                "confidence": confidence,
                "scores": {
                    "REAL": real_score,
                    "AI_GENERATED": ai_score
                },
                "analysis_details": results
            }
            
        except Exception as e:
            logger.error(f"Error combining results: {str(e)}")
            return {
                "label": "UNCERTAIN",
                "confidence": 0.5,
                "scores": {"REAL": 0.5, "AI_GENERATED": 0.5},
                "error": str(e)
            }
    
    # Helper methods for image analysis
    def calculate_sharpness(self, gray_image: np.ndarray) -> float:
        """Calculate image sharpness using Laplacian variance"""
        laplacian = cv2.Laplacian(gray_image, cv2.CV_64F)
        return laplacian.var()
    
    def estimate_noise(self, gray_image: np.ndarray) -> float:
        """Estimate noise level in the image"""
        # Simple noise estimation using median filter
        median = cv2.medianBlur(gray_image, 3)
        noise = cv2.absdiff(gray_image, median)
        return np.mean(noise)
    
    def calculate_saturation(self, image: np.ndarray) -> float:
        """Calculate image saturation"""
        hsv = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)
        return np.mean(hsv[:, :, 1])
    
    def calculate_pixel_uniformity(self, image: np.ndarray) -> float:
        """Calculate pixel uniformity (lower = more uniform)"""
        return np.std(image)
    
    def calculate_edge_density(self, image: np.ndarray) -> float:
        """Calculate edge density"""
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        edges = cv2.Canny(gray, 50, 150)
        return np.sum(edges > 0) / edges.size
    
    def calculate_texture_complexity(self, image: np.ndarray) -> float:
        """Calculate texture complexity"""
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        # Use GLCM-like features (simplified)
        return np.std(gray)
    
    def detect_artifacts(self, image: np.ndarray) -> bool:
        """Detect common AI generation artifacts"""
        # Check for unusual patterns
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        
        # Check for repetitive patterns
        fft = np.fft.fft2(gray)
        fft_shift = np.fft.fftshift(fft)
        magnitude = np.log(np.abs(fft_shift) + 1)
        
        # Look for regular patterns in frequency domain
        # This is a simplified artifact detection
        return False  # Placeholder
    
    # Model information methods
    def is_model_loaded(self) -> bool:
        return self.model_loaded
    
    def get_model_version(self) -> str:
        return self.model_version
    
    def get_model_type(self) -> str:
        return self.model_type
    
    def get_input_shape(self) -> Tuple[int, int, int]:
        return self.input_shape
    
    def get_classes(self) -> List[str]:
        return self.classes 