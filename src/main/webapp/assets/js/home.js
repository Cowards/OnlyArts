import http from "./http.js";
import getAccountInfo from "./account.js";

getAccountInfo();

const loadArtwork = async () => {
  const artworks = await http.send("GET", "/api/v2/artworks/search/getAll");
  console.log(artworks);
};

loadArtwork();
