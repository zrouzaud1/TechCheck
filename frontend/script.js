function fetchData() {
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
    // Store the search query in localStorage (can be used to pass data between pages)
    localStorage.setItem('searchQuery', query);
    // Navigate to the search results page
    window.location.href = 'search.html';
}

// Function to display the search query in search.html (optional)
window.onload = function() {
    if (window.location.pathname.endsWith('search.html')) {
        var query = localStorage.getItem('searchQuery');
        if (query) {
            // Display query or update search results based on the query
            console.log('Search query:', query);  // You can replace this with code to display or process the query
        }
    }
};
