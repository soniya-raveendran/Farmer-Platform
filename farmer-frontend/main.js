document.querySelector(".contact-form")
  .addEventListener("submit", function(e) {
    e.preventDefault();
    alert("Message sent successfully!");
  });

  document.querySelectorAll(".faq-question").forEach(question => {
  question.addEventListener("click", () => {
    const item = question.parentElement;
    item.classList.toggle("active");
  });
});
