package com.mhchlagou.moviecatalogservice.resources;



import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.mhchlagou.moviecatalogservice.model.CatalogItem;
import com.mhchlagou.moviecatalogservice.model.Movie;
import com.mhchlagou.moviecatalogservice.model.Rating;
import com.mhchlagou.moviecatalogservice.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder; 
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
	                
		UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);

        return userRating.getRatings().stream()
                .map(rating -> {
                	// For each movie ID, call movie info service and get details
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                    // Another way to call MicroServices with WebClient for Asynchr Transaction
                	/*Movie movie = webClientBuilder.build()
                			.get()
                			.uri("http://localhost:8082/movies/\" + rating.getMovieId()")
                			.retrieve()
                			.bodyToMono(Movie.class)
                			.block();*/
                    // Put them all together
                    return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                })
                .collect(Collectors.toList());
	                

	    }
}
