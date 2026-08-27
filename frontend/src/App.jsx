import { useRef, useState } from "react";
import axios from "axios";
import "./App.css";

const API_URL = "http://localhost:8082/api/scans";

function App() {
  const fileInputRef = useRef(null);

  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [dragging, setDragging] = useState(false);
  const [error, setError] = useState("");

  // -----------------------------------------
  // FILE SELECTION
  // -----------------------------------------

  const processFile = (file) => {
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setError("Please upload a valid image file.");
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setError("Image size should be less than 10 MB.");
      return;
    }

    setSelectedFile(file);
    setPreview(URL.createObjectURL(file));
    setResult(null);
    setError("");
  };

  const handleFileChange = (event) => {
    processFile(event.target.files[0]);
  };

  // -----------------------------------------
  // DRAG & DROP
  // -----------------------------------------

  const handleDragOver = (event) => {
    event.preventDefault();
    setDragging(true);
  };

  const handleDragLeave = () => {
    setDragging(false);
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setDragging(false);

    const file = event.dataTransfer.files[0];
    processFile(file);
  };

  // -----------------------------------------
  // ANALYZE
  // -----------------------------------------

  const handleAnalyze = async () => {
    if (!selectedFile) {
      setError("Please select a product image first.");
      return;
    }

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const formData = new FormData();

      // IMPORTANT:
      // Spring Boot expects @RequestParam("image")
      formData.append("image", selectedFile);

      const response = await axios.post(
          API_URL,
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
      );

      setResult(response.data);

      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });

    } catch (err) {
      console.error(err);

      if (err.response) {
        setError(
            err.response.data?.message ||
            `Server error: ${err.response.status}`
        );
      } else if (err.request) {
        setError(
            "Unable to connect to the backend. Make sure Spring Boot is running on port 8082."
        );
      } else {
        setError("Something went wrong while analyzing the product.");
      }

    } finally {
      setLoading(false);
    }
  };

  // -----------------------------------------
  // RESET
  // -----------------------------------------

  const resetAnalysis = () => {
    setSelectedFile(null);
    setPreview(null);
    setResult(null);
    setError("");

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  // -----------------------------------------
  // RISK HELPERS
  // -----------------------------------------

  const getRiskClass = (risk) => {
    if (!risk) return "unknown";

    const value = risk.toUpperCase();

    if (value === "HIGH") return "high";
    if (value === "MODERATE") return "moderate";
    if (value === "LOW") return "low";

    return "unknown";
  };

  const getRiskIcon = (risk) => {
    if (!risk) return "•";

    const value = risk.toUpperCase();

    if (value === "HIGH") return "!";
    if (value === "MODERATE") return "!";
    if (value === "LOW") return "✓";

    return "•";
  };

  // -----------------------------------------
  // UPLOAD PAGE
  // -----------------------------------------

  const renderUploadPage = () => {
    return (
        <>
          <section className="hero-section">

            <div className="hero-badge">
              <span className="pulse-dot"></span>
              AI-Powered Ingredient Intelligence
            </div>

            <h1>
              Know what you're
              <span> really eating.</span>
            </h1>

            <p>
              Upload a photo of your product label and get a detailed
              analysis of ingredients, allergens, health concerns and
              recommendations.
            </p>

          </section>

          <section className="upload-wrapper">

            <div
                className={`upload-box ${dragging ? "dragging" : ""}`}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
            >

              {!preview ? (
                  <>
                    <div className="upload-icon-wrapper">
                      <div className="upload-icon">↑</div>
                    </div>

                    <h2>Upload product label</h2>

                    <p>
                      Drag & drop your image here, or browse from your device
                    </p>

                    <button
                        className="browse-button"
                        onClick={() => fileInputRef.current?.click()}
                    >
                      Choose Image
                    </button>

                    <div className="upload-info">
                      JPG, PNG or WEBP · Maximum 10 MB
                    </div>
                  </>
              ) : (
                  <>
                    <div className="preview-wrapper">

                      <img
                          src={preview}
                          alt="Product preview"
                          className="product-preview"
                      />

                      <div className="preview-overlay">
                        <button
                            onClick={() =>
                                fileInputRef.current?.click()
                            }
                        >
                          Change image
                        </button>
                      </div>

                    </div>

                    <div className="selected-file">
                      <div className="file-check">✓</div>

                      <div>
                        <strong>{selectedFile?.name}</strong>
                        <small>
                          Image ready for analysis
                        </small>
                      </div>
                    </div>
                  </>
              )}

              <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  hidden
              />

            </div>

            {error && (
                <div className="error-message">
                  <span>!</span>
                  {error}
                </div>
            )}

            {selectedFile && (
                <button
                    className="analyze-main-button"
                    onClick={handleAnalyze}
                    disabled={loading}
                >
                  {loading ? (
                      <>
                        <span className="button-spinner"></span>
                        Analyzing product...
                      </>
                  ) : (
                      <>
                        Analyze Product
                        <span>→</span>
                      </>
                  )}
                </button>
            )}

          </section>

          <section className="feature-section">

            <div className="feature-card">
              <div className="feature-icon">⚠</div>
              <h3>Allergen Detection</h3>
              <p>
                Identify common allergens hidden in your ingredients.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">◉</div>
              <h3>AI Analysis</h3>
              <p>
                Understand the purpose and potential concerns of ingredients.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">✓</div>
              <h3>Smart Recommendations</h3>
              <p>
                Get simple recommendations based on the analysis.
              </p>
            </div>

          </section>
        </>
    );
  };

  // -----------------------------------------
  // RESULT PAGE
  // -----------------------------------------

  const renderResults = () => {
    const risk = result?.overallRiskLevel || "UNKNOWN";
    const riskClass = getRiskClass(risk);

    const ingredients = result?.ingredients || [];
    const allergens = result?.allergens || [];

    const highRiskCount = ingredients.filter(
        (item) =>
            item.riskLevel?.toUpperCase() === "HIGH"
    ).length;

    const moderateRiskCount = ingredients.filter(
        (item) =>
            item.riskLevel?.toUpperCase() === "MODERATE"
    ).length;

    const lowRiskCount = ingredients.filter(
        (item) =>
            item.riskLevel?.toUpperCase() === "LOW"
    ).length;

    return (
        <section className="results-page">

          {/* TOP RESULT HEADER */}

          <div className="result-top">

            <div>

              <div className="result-label">
                ANALYSIS COMPLETE
              </div>

              <h1>
                {result?.productName || "Unknown Product"}
              </h1>

              <div className="result-meta">
                <span>{result?.category || "FOOD"}</span>
                <span>•</span>
                <span>{ingredients.length} ingredients analyzed</span>
              </div>

            </div>

            <button
                className="new-analysis-button"
                onClick={resetAnalysis}
            >
              + New Analysis
            </button>

          </div>


          {/* SUMMARY */}

          <div className="summary-grid">

            <div className={`risk-summary ${riskClass}`}>

              <div className="summary-icon">
                {getRiskIcon(risk)}
              </div>

              <div>
                <span>OVERALL RISK</span>
                <strong>{risk}</strong>
              </div>

            </div>


            <div className="summary-card">

              <div className="summary-number">
                {ingredients.length}
              </div>

              <div>
                <span>INGREDIENTS</span>
                <strong>Analyzed</strong>
              </div>

            </div>


            <div className="summary-card allergen-summary">

              <div className="summary-number">
                {allergens.length}
              </div>

              <div>
                <span>ALLERGENS</span>
                <strong>Detected</strong>
              </div>

            </div>

          </div>


          {/* ALLERGEN WARNING */}

          {allergens.length > 0 && (
              <div className="allergen-section">

                <div className="warning-header">

                  <div className="warning-icon">
                    !
                  </div>

                  <div>
                    <h2>Allergens detected</h2>
                    <p>
                      Please review these ingredients carefully if you
                      have food allergies or sensitivities.
                    </p>
                  </div>

                </div>

                <div className="allergen-pills">

                  {allergens.map((allergen, index) => (
                      <div
                          className="allergen-pill"
                          key={index}
                      >
                        <span>⚠</span>
                        {allergen}
                      </div>
                  ))}

                </div>

              </div>
          )}


          {/* RISK DISTRIBUTION */}

          <div className="dashboard-card">

            <div className="card-heading">
              <div>
                <span className="heading-icon">◈</span>
                <div>
                  <h2>Risk overview</h2>
                  <p>
                    Ingredient risk distribution
                  </p>
                </div>
              </div>
            </div>

            <div className="risk-bars">

              <div className="risk-bar-row">
                <div>
                  <span className="risk-dot high-dot"></span>
                  High risk
                </div>

                <strong>{highRiskCount}</strong>

                <div className="bar">
                  <div
                      className="bar-fill high-fill"
                      style={{
                        width: ingredients.length
                            ? `${(highRiskCount / ingredients.length) * 100}%`
                            : "0%",
                      }}
                  ></div>
                </div>
              </div>


              <div className="risk-bar-row">
                <div>
                  <span className="risk-dot moderate-dot"></span>
                  Moderate
                </div>

                <strong>{moderateRiskCount}</strong>

                <div className="bar">
                  <div
                      className="bar-fill moderate-fill"
                      style={{
                        width: ingredients.length
                            ? `${(moderateRiskCount / ingredients.length) * 100}%`
                            : "0%",
                      }}
                  ></div>
                </div>
              </div>


              <div className="risk-bar-row">
                <div>
                  <span className="risk-dot low-dot"></span>
                  Low risk
                </div>

                <strong>{lowRiskCount}</strong>

                <div className="bar">
                  <div
                      className="bar-fill low-fill"
                      style={{
                        width: ingredients.length
                            ? `${(lowRiskCount / ingredients.length) * 100}%`
                            : "0%",
                      }}
                  ></div>
                </div>
              </div>

            </div>

          </div>


          {/* INGREDIENT ANALYSIS */}

          <div className="dashboard-card">

            <div className="card-heading">

              <div>
                <span className="heading-icon">✦</span>

                <div>
                  <h2>Ingredient analysis</h2>

                  <p>
                    Detailed information for each ingredient
                  </p>
                </div>
              </div>

            </div>


            <div className="ingredient-list">

              {ingredients.map((ingredient, index) => {

                const ingredientRisk =
                    getRiskClass(ingredient.riskLevel);

                return (
                    <div
                        className="ingredient-item"
                        key={index}
                    >

                      <div className="ingredient-header">

                        <div className="ingredient-title">

                          <div className="ingredient-number">
                            {String(index + 1).padStart(2, "0")}
                          </div>

                          <div>
                            <h3>
                              {ingredient.ingredient}
                            </h3>

                            {ingredient.allergen && (
                                <span className="ingredient-allergen">
                            ⚠ {ingredient.allergenType || "Allergen"}
                          </span>
                            )}
                          </div>

                        </div>


                        {ingredient.riskLevel && (
                            <span
                                className={`risk-tag ${ingredientRisk}`}
                            >
                        {ingredient.riskLevel}
                      </span>
                        )}

                      </div>


                      <div className="ingredient-details">

                        {ingredient.function && (
                            <div className="detail-block">
                              <span>FUNCTION</span>
                              <p>{ingredient.function}</p>
                            </div>
                        )}

                        {ingredient.benefits && (
                            <div className="detail-block">
                              <span>BENEFITS</span>
                              <p>{ingredient.benefits}</p>
                            </div>
                        )}

                        {ingredient.potentialConcerns && (
                            <div className="detail-block concern-block">
                              <span>POTENTIAL CONCERNS</span>
                              <p>{ingredient.potentialConcerns}</p>
                            </div>
                        )}

                        {ingredient.suitableFor && (
                            <div className="detail-block">
                              <span>SUITABLE FOR</span>
                              <p>{ingredient.suitableFor}</p>
                            </div>
                        )}

                        {ingredient.cautionFor && (
                            <div className="detail-block caution-block">
                              <span>CAUTION FOR</span>
                              <p>{ingredient.cautionFor}</p>
                            </div>
                        )}

                      </div>

                    </div>
                );
              })}

            </div>

          </div>


          {/* TWO COLUMN SECTION */}

          <div className="two-column">

            <div className="dashboard-card">

              <div className="card-heading">

                <div>
                  <span className="heading-icon">!</span>

                  <div>
                    <h2>Key concerns</h2>
                    <p>Important points to review</p>
                  </div>
                </div>

              </div>

              {result?.keyConcerns?.length > 0 ? (

                  <div className="concern-list">

                    {result.keyConcerns.map(
                        (concern, index) => (
                            <div
                                className="concern-item"
                                key={index}
                            >
                              <span>!</span>
                              <p>{concern}</p>
                            </div>
                        )
                    )}

                  </div>

              ) : (
                  <div className="empty-state">
                    ✓ No major concerns identified.
                  </div>
              )}

            </div>


            <div className="dashboard-card">

              <div className="card-heading">

                <div>
                  <span className="heading-icon">✦</span>

                  <div>
                    <h2>Recommendations</h2>
                    <p>What you should know</p>
                  </div>
                </div>

              </div>

              {result?.recommendations?.length > 0 ? (

                  <div className="recommendation-list">

                    {result.recommendations.map(
                        (recommendation, index) => (
                            <div
                                className="recommendation-item"
                                key={index}
                            >
                              <span>✓</span>
                              <p>{recommendation}</p>
                            </div>
                        )
                    )}

                  </div>

              ) : (
                  <div className="empty-state">
                    No specific recommendations.
                  </div>
              )}

            </div>

          </div>


          {/* OVERALL ASSESSMENT */}

          <div className={`assessment-box ${riskClass}`}>

            <div className="assessment-icon">
              {getRiskIcon(risk)}
            </div>

            <div>
              <span>OVERALL ASSESSMENT</span>

              <p>
                {result?.overallAssessment ||
                    "No overall assessment available."}
              </p>
            </div>

          </div>


          {/* BOTTOM */}

          <div className="bottom-action">

            <button
                className="new-analysis-large"
                onClick={resetAnalysis}
            >
              Analyze Another Product
              <span>→</span>
            </button>

          </div>

        </section>
    );
  };

  // -----------------------------------------
  // MAIN
  // -----------------------------------------

  return (
      <div className="app">

        <header className="navbar">

          <div className="brand">

            <div className="brand-mark">
              IA
            </div>

            <div>
              <strong>Ingredient</strong>
              <span>Analyzer</span>
            </div>

          </div>

          <div className="navbar-status">
            <span></span>
            AI Analysis Active
          </div>

        </header>


        <main className="main-container">

          {!result && !loading && renderUploadPage()}

          {loading && (
              <section className="loading-screen">

                <div className="loading-animation">
                  <div className="loading-ring"></div>
                  <span>IA</span>
                </div>

                <h1>Analyzing your product</h1>

                <p>
                  We're reading the label and analyzing the ingredients.
                </p>

                <div className="loading-steps">

                  <div className="active">
                    <span>✓</span>
                    Reading product label
                  </div>

                  <div className="active">
                    <span>✓</span>
                    Identifying ingredients
                  </div>

                  <div>
                    <span className="small-loader"></span>
                    Running AI analysis
                  </div>

                </div>

              </section>
          )}

          {result && !loading && renderResults()}

        </main>


        <footer className="footer">

          <div>
            <strong>Ingredient Analyzer</strong>
            <span> · AI-powered ingredient intelligence</span>
          </div>

          <div>
            Powered by AI · PostgreSQL
          </div>

        </footer>

      </div>
  );
}

export default App;