document
  .querySelector("#pw-dropdown-btn")
  .addEventListener("click", (event) => {
    document.querySelector(".dropdown-icon").innerHTML = document
      .querySelector("#pw-change-form-block")
      .classList.toggle("active")
      ? '<i class="bx bx-chevron-down"></i>'
      : '<i class="bx bx-chevron-right"></i>';
  });
document.querySelector("#btn-publish").addEventListener("click", (e) => {
  window.location.href = "./publish.html";
});
