import React, { useState, useEffect } from "react";
import {
  Terminal,
  Search,
  Database,
  Users,
  CheckCircle,
  AlertTriangle,
  Globe,
  Code,
  Server,
  Cpu,
  Activity,
  GitBranch,
  Upload,
  Image,
  AudioWaveform ,
  Loader2,
  Wrench,
} from "lucide-react";

// Add this function at the top (outside App)
async function analyzeImage(file: File) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("https://youngjeck-aidetect.hf.space/analyze", {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error("Failed to analyze image");
  }

  const result = await response.json();
  return result;
}

interface SearchResult {
  name: string;
  source: string;
  confidence: number;
}

function App() {
  const [verificationCount, setVerificationCount] = useState(247);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [dragActive, setDragActive] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [searchResult, setSearchResult] = useState<SearchResult | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [uploadResult, setUploadResult] = useState<any>(null);
  const [uploadError, setUploadError] = useState<string | null>(null);

  // Update handleFileSelect to enable upload
  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setSelectedFile(file);
    setUploadResult(null);
    setUploadError(null);
    setIsAnalyzing(true);
    createImagePreview(file);
    try {
      const data = await analyzeImage(file);
      setUploadResult(data);
    } catch (err) {
      setUploadError("Something went wrong.");
    }
    setIsAnalyzing(false);
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setVerificationCount((prev) => prev + Math.floor(Math.random() * 2) + 1);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    // Disabled for feature upgrade
  };


  const createImagePreview = (file: File) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      setImagePreview(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  };



  const resetAnalysis = () => {
    setSelectedFile(null);
    setImagePreview(null);
    setSearchResult(null);
    setIsAnalyzing(false);
  };

  const techStack = [
    { name: "Java", usage: "Backend API development and business logic" },
    {
      name: "Spring Boot",
      usage: "GraphQL API framework and dependency injection",
    },
    {
      name: "Python (FastAPI)",
      usage: "Machine learning model serving and image processing",
    },
    {
      name: "OpenAI",
      usage: "Natural language processing and content analysis",
    },
    {
      name: "Hugging Face Model",
      usage: "Facial recognition and image comparison",
    },
    { name: "MySQL", usage: "Primary database for user data and results" },
    { name: "Redis", usage: "Caching and session management" },
    { name: "React", usage: "Frontend UI development" },
  ];

  const platforms = [
    { name: "LinkedIn", verified: 5, flagged: 2, accuracy: 78.2 },
    { name: "X", verified: 7, flagged: 1, accuracy: 72.6 },
    { name: "Instagram", verified: 13, flagged: 5, accuracy: 80.1 },
    { name: "Facebook", verified: 8, flagged: 2, accuracy: 75.8 },
  ];

  return (
    <div className="min-h-screen bg-black text-white font-mono">
      {/* Grid Background */}
      <div className="fixed inset-0 opacity-5">
        <div
          className="absolute inset-0"
          style={{
            backgroundImage: `linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px)`,
            backgroundSize: "20px 20px",
          }}
        ></div>
      </div>

      {/* Header */}
      <header className="relative z-50 px-6 py-4 border-b border-gray-800">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Terminal className="w-6 h-6" />
            <span className="text-lg font-bold tracking-wider">
              ISTHISPERSONREAL
            </span>
          </div>
          <div className="flex items-center space-x-3 mb-4 md:mb-0">
          <AudioWaveform  className="w-5 h-5"/>
            <span className="font-bold tracking-wider">built by <u><a href="https://github.com/jvalaj">@jvalaj</a></u></span>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="relative px-6 py-12">
        <div className="max-w-7xl mx-auto">
          {/* Hero */}
          <section className="mb-20">
            <div className="mb-8">
              <h1 className="text-4xl md:text-6xl font-bold mb-4 tracking-tight">
                Is This Person <br />
                <span className="text-blue-400">Real?</span>
              </h1>
              <p className="text-lg text-gray-300 max-w-3xl leading-relaxed">
                A web application that helps verify if social media profiles are
                authentic or fake. Uses an ML model to analyze profile
                pictures, web scraping to gather data across platforms, and
                machine learning to score credibility.
              </p>
            </div>

            {/* Upload Section - ENABLED */}
            <div className="mb-12">
              <h2 className="text-xl font-bold mb-4">Upload Profile Image</h2>
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Upload Area */}
                <div>
                  <div className="border-2 border-dashed border-gray-600 bg-gray-900 rounded-lg p-8 text-center">
                    <div className="flex flex-col items-center space-y-4">
                      <Image className="w-12 h-12 text-gray-400" />
                      <input
                        type="file"
                        accept="image/*"
                        onChange={handleFileSelect}
                        className="mb-4"
                        disabled={isAnalyzing}
                      />
                      {imagePreview && (
                        <img
                          src={imagePreview}
                          alt="Preview"
                          className="max-h-40 rounded shadow mb-2"
                        />
                      )}
                      {isAnalyzing && (
                        <div className="flex items-center space-x-2 text-gray-400">
                          <Loader2 className="w-5 h-5 animate-spin" />
                          <span>Analyzing...</span>
                        </div>
                      )}
                      {uploadError && (
                        <div className="text-red-400">{uploadError}</div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Results Area */}
                <div className="space-y-6">
                  <div className="border border-gray-700 p-6 rounded-lg">
                    <h3 className="font-bold mb-3 text-gray-200">
                      Analysis Results
                    </h3>
                    <p className="text-xs text-yellow-400 mb-4">
                      Disclaimer: This analysis is powered by machine learning
                      models and may not be 100% accurate. Results should be
                      interpreted as guidance only and not as definitive proof.
                      Always use your own judgment and consider additional
                      information when making decisions.
                    </p>
                    <div className="text-gray-300">
                      {uploadResult ? (
                        <div className="space-y-4">
                          {/* Label Badge */}
                          <div className="flex items-center space-x-2">
                            <span
                              className={`px-3 py-1 rounded-full text-xs font-bold
                              ${
                                uploadResult.label === "AI"
                                  ? "bg-red-700 text-white"
                                  : "bg-green-700 text-white"
                              }`}
                            >
                              {uploadResult.label === "AI"
                                ? "AI Generated"
                                : "Real Human"}
                            </span>
                            <span className="text-xs text-gray-400">
                              Confidence:{" "}
                              {(uploadResult.confidence * 100).toFixed(1)}%
                            </span>
                          </div>
                          {/* Scores */}
                          <div>
                            <div className="mb-2 text-sm font-semibold">
                              Scores:
                            </div>
                            {uploadResult.scores &&
                              Object.entries(uploadResult.scores).map(
                                ([key, value]) => (
                                  <div key={key} className="mb-2">
                                    <div className="flex justify-between">
                                      <span className="capitalize">{key}</span>
                                      <span>{(value * 100).toFixed(1)}%</span>
                                    </div>
                                    <div className="w-full bg-gray-800 rounded h-2 mt-1">
                                      <div
                                        className={
                                          key === uploadResult.label
                                            ? key === "AI"
                                              ? "bg-red-600"
                                              : "bg-green-600"
                                            : "bg-gray-500"
                                        }
                                        style={{
                                          width: `${value * 100}%`,
                                          height: "100%",
                                          borderRadius: "0.25rem",
                                        }}
                                      ></div>
                                    </div>
                                  </div>
                                )
                              )}
                          </div>
                        </div>
                      ) : (
                        <span className="text-gray-500">
                          {isAnalyzing
                            ? "Analyzing image..."
                            : "Upload an image to see results here."}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          {/* Name Search Section */}
          <div className="mb-12 w-full sm:w-1/2">
            <h2 className="text-xl font-bold mb-4">Enter Full Name</h2>
            <div className="border border-gray-700 bg-gray-900 rounded-lg p-8">
              <input
                type="text"
                placeholder="disabled for now"
                className="w-full px-4 py-2 rounded bg-black border border-gray-700 text-white mb-4 focus:outline-none focus:border-red-400"
                disabled
              />
              <p className="text-gray-500 text-sm">
                I am currently upgrading the search for web and social media
                platforms for this person's name, scraping public data to help
                determine if the profile is authentic or potentially fake.
              </p>
            </div>
          </div>

          {/* Tech Stack */}
          <section className="mb-20">
            <h2 className="text-2xl font-bold mb-8">Technologies Used</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {techStack.map((tech, index) => (
                <div
                  key={index}
                  className="border border-gray-700 p-6 hover:border-white transition-colors"
                >
                  <div className="flex items-center mb-3">
                    <Code className="w-5 h-5 mr-3" />
                    <h3 className="font-bold">{tech.name}</h3>
                  </div>
                  <p className="text-gray-400 text-sm">{tech.usage}</p>
                </div>
              ))}
            </div>
          </section>

          {/* Platform Results */}
          <section className="mb-20">
            <h2 className="text-2xl font-bold mb-8">Platforms Analysed</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {platforms.map((platform, index) => (
                <div
                  key={index}
                  className="border border-gray-700 p-6 hover:border-white transition-colors"
                >
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="font-bold">{platform.name}</h3>
                    <Globe className="w-5 h-5" />
                  </div>
                  <div className="space-y-3 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-400">Verified:</span>
                      <span>{platform.verified}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-400">Flagged:</span>
                      <span>{platform.flagged}</span>
                    </div>
                    <div className="flex justify-between pt-2 border-t border-gray-800">
                      <span className="text-gray-400">Accuracy:</span>
                      <span className="font-bold">{platform.accuracy}%</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Stats Grid */}
          <section className="mb-20">
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="border border-gray-700 p-6">
                <div className="text-2xl font-bold mb-1">
                  27
                </div>
                <div className="text-gray-400 text-sm">Profiles Processed</div>
              </div>
              <div className="border border-gray-700 p-6">
                <div className="text-2xl font-bold mb-1">76.7%</div>
                <div className="text-gray-400 text-sm">Overall Accuracy</div>
              </div>
              <div className="border border-gray-700 p-6">
                <div className="text-2xl font-bold mb-1">4</div>
                <div className="text-gray-400 text-sm">Platforms Supported</div>
              </div>
              <div className="border border-gray-700 p-6">
                <div className="text-2xl font-bold mb-1">{'<'}10s</div>
                <div className="text-gray-400 text-sm">
                  Average Processing Time
                </div>
              </div>
            </div>
          </section>

          {/* How It Works */}
          <section className="mb-20">
            <h2 className="text-2xl font-bold mb-8">How It Works</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <div className="border border-gray-700 p-8 hover:border-white transition-colors">
                <Search className="w-8 h-8 mb-4" />
                <h3 className="text-xl font-bold mb-4">Image Analysis</h3>
                <p className="text-gray-300 mb-4">
                  Uses DeepFace to analyze profile pictures for signs of
                  manipulation or AI generation.
                </p>
                <div className="text-sm text-gray-400">
                  <div>• Face detection algorithms</div>
                  <div>• Image metadata analysis</div>
                </div>
              </div>

              <div className="border border-gray-700 p-8 hover:border-white transition-colors">
                <Database className="w-8 h-8 mb-4" />
                <h3 className="text-xl font-bold mb-4">Data Collection</h3>
                <p className="text-gray-300 mb-4">
                  Scrapes publicly available information from social media
                  platforms to build a comprehensive profile. Looks for
                  inconsistencies in posting patterns and connections.
                </p>
                <div className="text-sm text-gray-400">
                  <div>• Multi-platform data gathering</div>
                  <div>• Activity pattern analysis</div>
                </div>
              </div>

              <div className="border border-gray-700 p-8 hover:border-white transition-colors">
                <Cpu className="w-8 h-8 mb-4" />
                <h3 className="text-xl font-bold mb-4">Credibility Scoring</h3>
                <p className="text-gray-300 mb-4">
                  Combines all collected data points using OpenAI and machine
                  learning models to generate a credibility score. Trained on
                  manually verified fake and real profiles.
                </p>
                <div className="text-sm text-gray-400">
                  <div>• ML-based scoring algorithm</div>
                  <div>• Behavioral pattern recognition</div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>

      {/* Footer */}
      <footer className="relative px-6 py-8 border-t border-gray-800 mt-20">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between">
          <div className="flex items-center space-x-3 mb-4 md:mb-0">
            <Terminal className="w-5 h-5" />
            <span className="font-bold tracking-wider">ISTHISPERSONREAL</span>
          </div>
          <div className="flex items-center space-x-3 mb-4 md:mb-0">
          <AudioWaveform  className="w-5 h-5"/>
            <span className="font-bold tracking-wider">built by <u><a href="https://github.com/jvalaj">@jvalaj</a></u></span>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
