const signupForm = document.querySelector('#signup-form');
const signupErrorMessage = document.querySelector('#signup-error-message');
signupForm.addEventListener('submit', async event => {
    event.preventDefault();
    try {
        const firstname = document.querySelector('#firstname').value;
        const lastname = document.querySelector('#lastname').value;
        const email = document.querySelector('#email').value;
        const password = document.querySelector('#password').value;
        const repassword = document.querySelector('#repassword').value;
        const role = document.querySelector('#myDropdown').value;
        if (password !== repassword)
            throw new Error("Password does not match");
        const data = {
            "firstName": `${firstname}`,
            "lastName": `${lastname}`,
            "email": `${email}`,
            "password": `${password}`,
            "roleId": `${role}`
        };
        const token = await send('POST',
                'http://localhost:8080/OnlyArts/api/v1/authentication/register',
                data);
        localStorage.setItem("authtoken", `${token}`);
        window.location.href = `${api}/index.html`;
    } catch (error) {
        error = error.toString().replace('Error: ', '');
        signupErrorMessage.innerHTML = `${error}`;
    }
});

