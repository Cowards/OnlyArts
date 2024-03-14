document.querySelector("#image").addEventListener("change", (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.addEventListener("load", (event) => {
      document.querySelector("#imagePreview").src = event.target.result;
    });
    reader.readAsDataURL(file);
  }
});
