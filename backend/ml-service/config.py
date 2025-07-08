import os
from typing import Optional
from pydantic import BaseSettings

class Settings(BaseSettings):
    """Application settings"""
    
    # Server settings
    host: str = "0.0.0.0"
    port: int = 8000
    debug: bool = False
    
    # Model settings
    model_path: Optional[str] = None
    model_version: str = "1.0.0"
    confidence_threshold: float = 0.6
    
    # Processing settings
    max_file_size: int = 10 * 1024 * 1024  # 10MB
    supported_formats: list = ["image/jpeg", "image/jpg", "image/png", "image/gif"]
    
    # Logging
    log_level: str = "INFO"
    log_file: Optional[str] = None
    
    # Security
    api_key: Optional[str] = None
    enable_auth: bool = False
    
    # Performance
    max_workers: int = 4
    timeout: int = 30
    
    # Redis settings (for caching)
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_password: Optional[str] = None
    redis_db: int = 0
    
    # Database settings (if needed)
    database_url: Optional[str] = None
    
    class Config:
        env_file = ".env"
        case_sensitive = False

# Create settings instance
settings = Settings() 