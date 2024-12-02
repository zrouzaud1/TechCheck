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

// Handle DOMContentLoaded
document.addEventListener('DOMContentLoaded', function () {
    const openSignupBtn = document.getElementById('openSignupBtn');
    const signupPopup = document.getElementById('signupPopup');
    const closeSignupBtn = document.querySelector('.signup-close-btn');

    if (openSignupBtn && signupPopup) {
        openSignupBtn.addEventListener('click', function () {
            signupPopup.style.display = 'flex';
        });

        if (closeSignupBtn) {
            closeSignupBtn.addEventListener('click', function () {
                signupPopup.style.display = 'none';
            });
        } else {
            console.error("Element with class 'signup-close-btn' not found.");
        }

        window.addEventListener('click', function (event) {
            if (event.target === signupPopup) {
                signupPopup.style.display = 'none';
            }
        });
    } else {
        console.error("Elements 'openSignupBtn' or 'signupPopup' not found.");
    }
});

// Navigation functions
function goHome() {
    window.location.href = 'index.html';
}

function signUp() {
    window.location.href = 'signup.html';
}

function postReview() {
    window.location.href = 'post_review.html'; // Redirect to the review submission page
}

// Function to open the search pop-up
function openSearch() {
    const searchPopup = document.getElementById('searchPopup');
    if (searchPopup) {
        searchPopup.style.display = 'flex';
    } else {
        console.warn("Element with ID 'searchPopup' not found.");
    }
}

// Function to close the search pop-up
function closeSearch() {
    const searchPopup = document.getElementById('searchPopup');
    if (searchPopup) {
        searchPopup.style.display = 'none';
    } else {
        console.warn("Element with ID 'searchPopup' not found.");
    }
}

// Handle Review Form Submission
const reviewForm = document.getElementById('reviewForm');
if (reviewForm) {
    reviewForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const productName = document.getElementById('productName').value;
        const review = document.getElementById('review').value;

        fetch('http://localhost:8080/addReview', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `productName=${encodeURIComponent(productName)}&review=${encodeURIComponent(review)}`,
        })
            .then(response => response.text())
            .then(data => {
                document.getElementById('reviewResponse').innerText = data;
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('reviewResponse').innerText = "Failed to submit review.";
            });
    });
} else {
    console.warn("Element with ID 'reviewForm' not found.");
}

// Handle Search Query Submission
function key_up(event) {
    event = event || window.event;
    if (event.keyCode === 13) { // Check if Enter key is pressed
        submitSearch();
    }
}

function submitSearch() {
    const searchInput = document.getElementById('navbarSearchInput') || document.getElementById('popupSearchInput');

    if (!searchInput || !searchInput.value.trim()) {
        alert("Please enter a search term.");
        return;
    }

    const query = searchInput.value.trim();
    console.log(`Submitting search for: ${query}`); // Debug log

    // Store the search query in localStorage
    localStorage.setItem('searchQuery', query);

    fetch(`http://localhost:8080/data?query=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                console.error("Backend responded with an error:", response.statusText);
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log('Fetched search results from backend:', data); // Debug log
            localStorage.setItem('searchResults', JSON.stringify(data)); // Optional if needed
            window.location.href = 'search.html';
        })
        .catch(error => {
            console.error("Error during fetch:", error);
            alert("Failed to fetch search results.");
        });
}

// Display Search Results in search.html
document.addEventListener('DOMContentLoaded', function () {
    const productName = localStorage.getItem('searchQuery');  // Retrieve product name
    console.log(`Loading results for query: ${productName}`); // Debug log

    const searchResultsDiv = document.getElementById('searchResults');
    if (!searchResultsDiv) {
        console.error("Element with ID 'searchResults' not found.");
        return;
    }

    if (productName) {
        fetch(`http://localhost:8080/data?query=${encodeURIComponent(productName)}`)
            .then(response => response.json())
            .then(data => {
                console.log('Fetched search data:', data); // Debug log

                if (data.length > 0) {
                    data.forEach(review => {
                        const resultRow = document.createElement('div');
                        resultRow.classList.add('result-row');
                        resultRow.innerText = review;
                        searchResultsDiv.appendChild(resultRow);
                    });
                } else {
                    searchResultsDiv.innerText = "No reviews found for this product.";
                }
            })
            .catch(error => {
                console.error('Error fetching search results:', error);
                searchResultsDiv.innerText = "Error fetching search results.";
            });
    } else {
        searchResultsDiv.innerText = "No search query provided.";
    }
});
