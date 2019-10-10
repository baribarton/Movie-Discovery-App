# Movie-Discovery-App
Vizio App Challenge: Movie Discovery App

Objective: Create a movie discovery application, that displays movie poster images fetched from the The Movie DB service API specified below.
Requirements: 
·	Display movie poster images from the Movie DB API. Use the date format: YYYY-MM-DD to specify the date range, which should be at least three weeks. Refer to the API documentation for more details. The app should be responsive, crash free and elegantly handle network and service errors.
·	Implement at least two bonus features

API Documentation:
 https://developers.themoviedb.org/3

Image Base URL:
https://image.tmdb.org/t/p/w500

Example Movie URL:
https://api.themoviedb.org/3/discover/movie?api_key=64b6f3a69e5717b13ed8a56fe4417e71&language=en-US&sort_by=release_date.desc&include_adult=false&include_video=false&page=1&release_date.gte=2019-07-18&release_date.lte=2019-08-18

Example Image URL:
https://image.tmdb.org/t/p/w500/h0O3e35W0xnkEaL8iXYvqYkZrRB.jpg

What we are looking for:
●	Application Architecture
●	Readability. This is really important, we would like to see comments and variables named descriptively.
●	App responsiveness. There should be no lag or jittering when scrolling.
●	Efficient memory usage. Use an image cache to reduce load on memory.
●	How do you write clean code and how would this code scale?
●	Testing. Understanding where to test and what to test.
●	Error handling. Service and network errors are common and your code should notify the user of service or network issues.
●	DRY principle
●	Efficient use of technology. What libraries can you use that have already been written to help you?
 
** We would like to see at least 2 of the bonus features list below:
 
Bonus features:
·	Movie Detail page (using Get Details endpoint)
·	Load multiple pages, using the page parameter in the Movie DB fetch URL. After a page of movies has been displayed, allow the user to load another page, or do so automatically with pull to refresh.
·	Date picker, for selecting movie date range
·	Search movies by title
o	Display by release date, descending
·	Save movies to a “Favorites list”, that persist across app launches
·	Display movie reviews
·	Filter list of movies by genre
·	Other feature not listed here that would set your app apart
