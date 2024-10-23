function fetchData() {
    // This will call /data without any query parameters
    fetch('http://localhost:8080/data')
        .then(response => response.text())
        .then(data => {
            // Display the response in the HTML
            document.getElementById('response').innerText = data;
        })
        .catch(error => console.error('Error:', error));
}

// Function to open the search pop-up
function openSearch() {
    document.getElementById('searchPopup').style.display = 'flex';
}

// Function to close the search pop-up
function closeSearch() {
    document.getElementById('searchPopup').style.display = 'none';
}

// Function to submit the search query and navigate to the search results page
function submitSearch() {
    var query = document.getElementById('searchInput').value;

    // Store the search query in localStorage to use on the search results page
    localStorage.setItem('searchQuery', query);

    // Navigate to the search.html page (you can later use this query on that page)
    window.location.href = 'search.html';

    // Fetch request for search results (optional, if you need results before navigating)
    fetch(`http://localhost:8080/data?query=${query}`)  // Call with search query
        .then(response => response.json())
        .then(data => {
            console.log('Search results:', data);  // Log or handle the search results here
            document.getElementById('response').innerText = JSON.stringify(data);
        })
        .catch(error => console.error('Error:', error));
}


// Function to display the search query in search.html
window.onload = function() {
    if (window.location.pathname.endsWith('search.html')) {
        var query = localStorage.getItem('searchQuery');
        if (query) {
            // Backend Pull for later goes here
            console.log('Search query:', query);
        }
    }
};
