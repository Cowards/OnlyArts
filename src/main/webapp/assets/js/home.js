const main = document.querySelector("#artworks");
const loading = async () => {
  try {
    const artworks = await sendJson("GET", `${api}/api/v2/artworks`);
    artworks.forEach(async (element) => {
      const imageData = await send(
        "GET",
        `${api}/api/v1/image/${element.artworkImage}`
      );
      const owner = await sendJson(
        "GET",
        `${api}/api/v4/user/${element.ownerId}`
      );
      const react = await sendJson(
        "GET",
        `${api}/api/v2/reaction/${element.imageId}`
      );

      main.innerHTML += `
        <div class="card">
          <img calss="card-img" src="${imageData}" alt="" />
          <a class="card-info" href="#">
            <div class="card-info-top">
              <h2>${element.name}</h2>
            </div>
            <div class="card-info-bottom">
              <div class="card-bottom-left">${owner.firstName} ${owner.lastName}</div>
              <div class="card-bottom-right">
                <i class="bx bxs-heart"></i>
                <p>${react}</p>
              </div>
            </div>
          </a>
        </div>
      `;
    });
  } catch (error) {
    console.log(error);
  }
};

// document.addEventListener("DOMContentLoaded", async () => {
//   fetch("./assets/components/card-item.html")
//     .then(async (response) => await response.text())
//     .then(async (data) => {
//       const cardHolder = (document.querySelector(".card-holder").innerHTML =
//         data);
//     });
// });
