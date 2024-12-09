let getData;

async function getResponseData() {
    const responseData = await fetch("http://localhost:8080/jira-excel-data")
    const responseJsonData = await responseData.json();
    return responseJsonData
}

async function fetchDataAndStore() {
    getData = await getResponseData(); // Wait for the data to be fetched and assigned
    const paginatedData = await mapResponseDataWithPage(getData);
    const table = document.querySelector("table");
    const paginationContainer = document.querySelector(".pagination-container");

    for (let key of paginatedData.keys()) {
        const span = document.createElement("span");
        span.classList.add('page-item');
        span.textContent = key;

        span.addEventListener("click", function (event) {

            const rows = table.querySelectorAll("tr:not(:first-child)");

            // Remove each data row
            rows.forEach(element => {
                element.remove();
            });
            // table.innerHTML = "";

            const page = Number(event.target.textContent);
            for (let k = 0; k < paginatedData.get(page).length; k++) {
                const tr = document.createElement("tr");

                // Because each ExcelColum nsDto represent key - value
                for (let keyValue in paginatedData.get(page)[k]) {
                    const td = document.createElement("td");
                    td.textContent = paginatedData.get(page)[k][keyValue];
                    tr.appendChild(td);
                }
                table.appendChild(tr);
            }
        });
        paginationContainer.appendChild(span);
    }

    await loadingFirstPage(paginatedData, table);

    // for (let k = 0; k < paginatedData.get(1).length; k++) {
    //     const tr = document.createElement("tr");
    //     for (let keyValue in paginatedData.get(1)[k]) {
    //         console.log(paginatedData.get(1)[k][keyValue]);
    //         const td = document.createElement("td");
    //         td.textContent = paginatedData.get(1)[k][keyValue];
    //         tr.appendChild(td);
    //     }
    //     table.appendChild(tr);
    // }
}

// for (let index = 0; index < getData.length; index++) {
//     const tr = document.createElement("tr");

//     console.log(getData[index]);

//     for (let key in getData[index]) {
//         const td = document.createElement("td");
//         td.textContent = getData[index][key];
//         tr.appendChild(td);
//     }


//     table.appendChild(tr);
// }


fetchDataAndStore();

// A map show items of each page
function mapResponseDataWithPage(responseData) {
    const pageNumber = pagingNumberOfPageAndItem(responseData.length, 10);
    const pageWithRespectingItems = new Map();
    let i = 0;
    for (let k of pageNumber.keys()) {
        let arrItems = [];
        for (let j = 0; j < pageNumber.get(k); j++) {
            arrItems.push(responseData[i++]);
        }
        pageWithRespectingItems.set(k, arrItems);
    }
    return pageWithRespectingItems;

}

function loadingFirstPage(paginatedData, table) {
    for (let k = 0; k < paginatedData.get(1).length; k++) {
        const tr = document.createElement("tr");

        for (let keyValue in paginatedData.get(1)[k]) {
            const td = document.createElement("td");
            td.textContent = paginatedData.get(1)[k][keyValue];
            tr.appendChild(td);
        }
        table.appendChild(tr);
    }
}

// A Map data structure shows how many item each page has (Map<Integer, List<ExcelColumnDto>)
function pagingNumberOfPageAndItem(totalItem, numberItemPerPage) {
    const result = new Map(); // Using Map to match Java's HashMap behavior

    const numberPages = Math.floor(totalItem / numberItemPerPage); // Integer division (floor)
    const numberItemOfLastPage = totalItem - (numberPages * numberItemPerPage); // Remaining items

    // Fill each page with `numberItemPerPage` items
    for (let i = 1; i <= numberPages; i++) {
        result.set(i, numberItemPerPage); // Add to the Map
    }
    // Add the last page with the remaining items, if any
    if (numberItemOfLastPage > 0) {
        result.set(numberPages + 1, numberItemOfLastPage);
    }
    return result; // Return the Map
}




