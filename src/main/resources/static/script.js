const contentTable = document.getElementById("contentTable");
const userButton = document.getElementById("users");
const requestButton = document.getElementById("requests");
const toiletButton = document.getElementById("toilets");

document.addEventListener("DOMContentLoaded", ()=>{

    userButton.addEventListener("click", ()=>{
        fetchUsers();
    })








});

async function fetchUsers(){
    const response = await fetch("http://localhost:8080/api/v1/users");
    if (!response.ok){
        console.error("Error: " + response.statusText);
    }
    const data = await response.json();

    contentTable.innerHTML="";

    const table = document.createElement("table");
    table.innerHTML = `<th>Nickname</th><th>Points</th>`
    data.forEach((data) => {
        const row = document.createElement("tr");
        row.innerHTML =
            `
                <td>${data.nickname}</td>
                <td>${data.contributionPoints}</td>
            `
        table.appendChild(row);
    });
    contentTable.appendChild(table);
}