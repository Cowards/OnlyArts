const load = async () => {
  const data = await sendJson("GET", `${api}/api/v3/dashboard/admin`);
  console.log(data);
  document.querySelector(
    "#artwork-count"
  ).innerHTML = `${data.artworks.length}`;
  document.querySelector("#user-count").innerHTML = `${data.users.length}`;
  const users = data.users;
  const artworks = data.artworks;
  const usersTable = document.querySelector("#user-data-body");
  const artworksTable = document.querySelector("#artwork-data-body");
  users.forEach(async (item) => {
    usersTable.innerHTML += `
                <tr>
                  <td>
                    <img src="./assets/images/user.png"/>
                    <p>${item.firstName} ${item.lastName}</p>
                  </td>
                  <td>${new Date(item.joinDate).toDateString()}</td>
                  <td>
                    ${
                      item.online
                        ? `<span class="status completed">Online</span>`
                        : `<span class="status pending">Offline</span>`
                    }
                  </td>
                </tr>
    `;
  });
  artworks.forEach(async (item) => {
    artworksTable.innerHTML += `
                <tr>
                  <td>
                    <img src="./assets/images/user.png"/>
                    <p>${item.name}</p>
                  </td>
                  <td>${new Date(item.releasedDate).toDateString()}</td>
                  <td>
                    ${
                      item.private
                        ? `<span class="status pending">Private</span>`
                        : `<span class="status completed">Public</span>`
                    }
                  </td>
                </tr>
    `;
  });
};

load();
