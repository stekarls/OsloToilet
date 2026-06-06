const contentTable = document.getElementById("contentTable");
const userButton = document.getElementById("users");
const requestButton = document.getElementById("requests");
const toiletButton = document.getElementById("toilets");
const reviewButton = document.getElementById("reviews");
const featuresButton = document.getElementById("features");

document.addEventListener("DOMContentLoaded", ()=>{

    contentTable.addEventListener("click", (event)=>{
        const targetRow = event.target.closest("tr");
        if (!targetRow) return;
        if (targetRow.parentElement.tagName === 'THEAD') return;
        showToilet(targetRow.id);


    });

    userButton.addEventListener("click", ()=>{
        fetchUsers();
    });

    featuresButton.addEventListener("click", ()=>{
        fetchFeatures();
    });

    toiletButton.addEventListener("click", ()=>{
        fetchToilets();
    });

    requestButton.addEventListener("click", ()=>{
        fetchRequests();
    });

    reviewButton.addEventListener("click", ()=>{
        fetchReviews();
    });



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
        row.id = data.id;
        row.innerHTML =
            `
                <td>${data.nickname}</td>
                <td>${data.contributionPoints}</td>
            `
        table.appendChild(row);
    });
    const heading = document.createElement("h2");
    heading.textContent = "Users"
    contentTable.appendChild(heading);
    contentTable.appendChild(table);
}

async function fetchFeatures(){
    const response = await fetch("/api/v1/feature");
    if (!response.ok){
        console.error("Error: " + response.statusText);
    }

    const data = await response.json();

    contentTable.innerHTML="";

    const table = document.createElement("table");
    table.innerHTML = `<th>Id</th><th>Feature</th>`
    data.forEach((data) => {
        const id = String(data.id);
        const str = id.substring(0,8);
        const row = document.createElement("tr");
        row.innerHTML =
            `
                <td>${str}</td>
                <td>${data.code}</td>
            `
        table.appendChild(row);
    });
    const heading = document.createElement("h2");
    heading.textContent = "Features"
    contentTable.appendChild(heading);
    contentTable.appendChild(table);
}

async function fetchToilets(){
    const response = await fetch("/api/v1/toilet");
    if (!response.ok){
        console.error("Error: " + response.statusText);
    }

    const data = await response.json();

    contentTable.innerHTML="";

    const table = document.createElement("table");
    table.innerHTML = `<th>Id</th><th>Name</th><th>Added</th>`
    data.forEach((data) => {

        const apiDate = new Date(data.added);
        const readableDate = apiDate.toLocaleString("no-NO", {
            dateStyle: "short",
        });

        const id = String(data.id);
        const str = id.substring(0, 8);
        const row = document.createElement("tr");
        row.innerHTML =
            `
                <td>${str}</td>
                <td>${data.name}</td>
                <td>${readableDate}</td>
            `
        table.appendChild(row);
        row.id = data.id;
    });
    const heading = document.createElement("h2");
    heading.textContent = "Toilets"
    contentTable.appendChild(heading);
    contentTable.appendChild(table);
}

async function fetchRequests() {
    const response = await fetch("/api/v1/contribution");
    if (!response.ok) {
        console.error("Error: " + response.statusText);
    }

    const data = await response.json();

    contentTable.innerHTML = "";

    const table = document.createElement("table");
    table.innerHTML = `<th>Id</th><th>Name</th><th>Created</th><th>Updated</th><th>Status</th>`
    data.forEach((data) => {
        const id = String(data.id);
        const str = id.substring(0, 8);
        const row = document.createElement("tr");
        row.id = data.id;
        row.innerHTML =
            `
                <td>${str}</td>
                <td>${data.name}</td>
                <td>${data.createdAt}</td>
                <td>${data.updatedAt}</td>
                <td>${data.requestStatus}</td>
            `
        table.appendChild(row);
    });
    const heading = document.createElement("h2");
    heading.textContent = "Location Requests"
    contentTable.appendChild(heading);
    contentTable.appendChild(table);
}

async function fetchReviews() {
    const response = await fetch("/api/v1/reviews");
    if (!response.ok) {
        console.error("Error: " + response.statusText);
    }

    const data = await response.json();

    contentTable.innerHTML = "";

    const table = document.createElement("table");
    table.innerHTML = `<th>Id</th><th>User</th><th>Cleanliness</th><th>Equipment</th><th>Access</th><th>AverageRating</th><th>Created</th>`
    data.forEach((data) => {

        const apiDate = new Date(data.created);
        const readableDate = apiDate.toLocaleString("no-NO", {
            dateStyle: "short",
        });


        const id = String(data.id);
        const str = id.substring(0, 8);
        const row = document.createElement("tr");
        row.id = data.id;
        row.innerHTML =
            `
                <td>${str}</td>
                <td>${data.userName}</td>
                <td>${data.cleanliness}</td>
                <td>${data.equipment}</td>
                <td>${data.access}</td>
                <td>${data.averageRating.toFixed(1)}</td>
                <td>${readableDate}</td>
            `
        table.appendChild(row);
    });
    const heading = document.createElement("h2");
    heading.textContent = "Reviews"
    contentTable.appendChild(heading);
    contentTable.appendChild(table);
}




function myMap(lat, lng) {
    var toilet = new google.maps.LatLng(lat, lng);


    var mapProp = {
        center: toilet,
        zoom: 15,
    };

    var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

    var marker = new google.maps.Marker({
        position: toilet,
        map: map,
        title: 'Toilet'
    });
}

async function showToilet(id) {
    try {
        const response = await fetch(`/api/v1/toilet/${id}`);
        if (!response.ok) throw new Error(response.statusText);

        const data = await response.json();

        contentTable.innerHTML = `
            <h2>Toilet</h2>
            <button class="button delete">Delete Toilet</button>
            <button class="button edit">Edit toilet</button>
            <p><strong>Id:</strong> ${data.id}</p>
            <p><strong>Name:</strong> ${data.name}</p>
            <p><strong>Latitude:</strong> ${data.latitude}</p>
            <p><strong>Longitude:</strong> ${data.longitude}</p>
            <p><strong>HasFee:</strong> ${data.hasFee}</p>
            <p><strong>Fee:</strong> ${data.fee}</p>
            <p><strong>Description:</strong> ${data.description}</p>
            <p><strong>HasConditions:</strong> ${data.hasConditions}</p>
            <p><strong>Conditions:</strong> ${data.conditions}</p>
            <p><strong>IsSeasonal:</strong> ${data.seasonal}</p>
            <p><strong>IsClosed:</strong> ${data.closed}</p>
            <p><strong>Added:</strong> ${data.added}</p>
            <p><strong>Updated:</strong> ${data.updatedAt}</p>
            <div id="googleMap" style="width: 50%; height: 400px;"></div>
        `;
        myMap(data.latitude, data.longitude);

    } catch (error) {
        console.error("Feil ved henting av toalett:", error);
    }
}

