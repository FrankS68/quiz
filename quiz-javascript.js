// Deine Quiz-Daten (Auszug)
const questions = [
  {
    id: 1,
    question: "Was bedeutet die 1:1-Regel?",
    options: [
      "Abstand = Höhe",
      "Abstand = halbe Höhe",
      "Abstand = 100 Meter"
    ],
    correct: 0,
    rationale: "Der horizontale Abstand muss mindestens der Flughöhe entsprechen."
  }
  // ... hier die weiteren 129 Fragen einfügen
];

let currentQuestionIndex = 0;
let score = 0;

function loadQuestion() {
  const q = questions[currentQuestionIndex];
  document.getElementById("question-text").innerText = q.question;
  
  const optionsContainer = document.getElementById("options");
  optionsContainer.innerHTML = ""; // Altes Quiz löschen

  q.options.forEach((opt, index) => {
    const btn = document.createElement("button");
    btn.innerText = opt;
    btn.onclick = () => checkAnswer(index);
    optionsContainer.appendChild(btn);
  });
}

function checkAnswer(selectedIndex) {
  const q = questions[currentQuestionIndex];
  const resultArea = document.getElementById("result");

  if (selectedIndex === q.correct) {
    score++;
    resultArea.innerHTML = `<p style="color:green">Richtig! ${q.rationale}</p>`;
  } else {
    resultArea.innerHTML = `<p style="color:red">Falsch. ${q.rationale}</p>`;
  }

  // Nächste Frage nach kurzer Pause
  setTimeout(() => {
    currentQuestionIndex++;
    if (currentQuestionIndex < questions.length) {
      resultArea.innerHTML = "";
      loadQuestion();
    } else {
      document.getElementById("quiz-container").innerHTML = 
        `<h2>Quiz beendet!</h2><p>Du hast ${score} von ${questions.length} Fragen richtig.</p>`;
    }
  }, 2000);
}

// Start
window.onload = loadQuestion;