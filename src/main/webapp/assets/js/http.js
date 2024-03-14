class Http {
  constructor() {
    this.api = "http://localhost:8080/OnlyArts";
  }
  async send(method, url, body) {
    try {
      const response = await fetch(this.api + url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          authtoken: localStorage.getItem("authtoken") || null,
        },
        body: body ? JSON.stringify(body) : undefined,
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message);
      }
      const data = await response.text();
      return data;
    } catch (error) {
      throw error;
    }
  }
}
export default new Http();
