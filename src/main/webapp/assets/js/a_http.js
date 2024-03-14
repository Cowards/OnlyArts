"use strict";
const api = "http://localhost:8080/OnlyArts";
const send = async (method, url, body) => {
  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
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
};
export default send;
