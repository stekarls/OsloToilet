const contentTable = document.getElementById("contentTable");
const userButton = document.getElementById("users");
const requestButton = document.getElementById("requests");
const toiletButton = document.getElementById("toilets");
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
        const id = String(data.id);
        const str = id.substring(0, 8);
        const row = document.createElement("tr");
        row.innerHTML =
            `
                <td>${str}</td>
                <td>${data.name}</td>
                <td>${data.added}</td>
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


function myMap(lat, lng) {
    var toilet = new google.maps.LatLng(lat, lng);


    var mapProp = {
        center: toilet,
        zoom: 15,
    };

    // Opprett kartet
    var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

    // Legg til en markør
    var marker = new google.maps.Marker({
        position: toilet,
        map: map,
        title: 'London'
    });
}

async function showToilet(id){
    const response = await fetch(`/api/v1/toilet/${id}`);
    if (!response.ok){
        console.error("Error: " + response.statusText);
    }

    const data = await response.json();

    contentTable.innerHTML="";

    const heading = document.createElement("h2");
    heading.textContent = "Toilet ";
    contentTable.appendChild(heading);

    const toiletId = document.createElement("p");
    toiletId.textContent = `Id: ${data.id}`
    contentTable.appendChild(toiletId);

    const name = document.createElement("p");
    name.textContent = `Name: ${data.name}`
    contentTable.appendChild(name);

    const latitude = document.createElement("p");
    latitude.textContent = `Latitude: ${data.latitude}`
    contentTable.appendChild(latitude);

    const longitude = document.createElement("p");
    longitude.textContent = `Longitude: ${data.longitude}`
    contentTable.appendChild(longitude);

    const hasFee = document.createElement("p");
    hasFee.textContent = `HasFee: ${data.hasFee}`
    contentTable.appendChild(hasFee);

    const fee = document.createElement("p");
    fee.textContent = `Fee: ${data.fee}`
    contentTable.appendChild(fee);

    const description = document.createElement("p");
    description.textContent = `Description: ${data.description}`
    contentTable.appendChild(description);

    const hasConditions = document.createElement("p");
    hasConditions.textContent = `HasConditions: ${data.hasConditions}`
    contentTable.appendChild(hasConditions);

    const conditions = document.createElement("p");
    conditions.textContent = `Conditions: ${data.conditions}`
    contentTable.appendChild(conditions);

    const isSeasonal = document.createElement("p");
    isSeasonal.textContent = `IsSeasonal: ${data.seasonal}`
    contentTable.appendChild(isSeasonal);

    const isClosed = document.createElement("p");
    isClosed.textContent = `IsClosed: ${data.closed}`
    contentTable.appendChild(isClosed);

    const added = document.createElement("p");
    added.textContent = `Added: ${data.added}`
    contentTable.appendChild(added);

    const updated = document.createElement("p");
    updated.textContent = `Updated: ${data.updatedAt}`
    contentTable.appendChild(updated);

    const map = document.createElement("div");
    map.id = "googleMap";
    map.style.width = "50%";
    map.style.height = "400px";
    contentTable.appendChild(map);
    myMap(data.latitude, data.longitude);
}
