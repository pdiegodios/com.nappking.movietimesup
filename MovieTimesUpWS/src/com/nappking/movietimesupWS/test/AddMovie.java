package com.nappking.movietimesupWS.test;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


import com.nappking.movietimesupWS.model.Movie;
import com.nappking.movietimesupWS.util.PSON;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;


public class AddMovie {
	private static String SAVE = "SAVE";
	private static String UPDATE = "UPDATE";
	public static void main(String[] args) {
		
		//VALUES
		String action = SAVE;
		
		int id = 51;
		int points = 1;
		int cinema = 4;
		int year = 2008;
		boolean masterpiece = false;
		boolean cult = true;
		String title = "El caballero oscuro";
		String alternative_title = "El caballero de la noche";
		String original_title = "The Dark Knight";
		String continent = "AM";
		String country = "Estados Unidos";
		String director = "Christopher Nolan";
		String genre = "Thriller";
		String poster = "http://ia.media-imdb.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SY317_CR0,0,214,317_.jpg";
		String  filmaffinity_id = "867354";
		String imdb_id = "tt0468569";
		String actor1="Christian Bale";
		String actor2="Heath Ledger";
		String actor3="Aaron Eckhart";
		String character1="Bruce Wayne";
		String character2="Joker";
		String character3="Harvey Dent";
		String other1 ="el icónico actor que ejerce de antagonista murió de sobredosis de ansiolíticos meses antes del estreno de la película";
		String other2 ="El rodaje tuvo lugar principalmente en Chicago, aunque también en Hong Kong, Los Angeles y distintas localizaciones de Inglaterra";
		String other3 ="Es la primera película sobre el personaje que no lleve su nombre en el título";
		String quote1="O mueres como un héroe, o vives lo suficiente para verte convertido en el villano";
		String quote2="¿Qué te parece un truco de magia? Voy a hacer que este lápiz desaparezca... ¡TARÁAAAAA!¡Ha desaparecido!";
		String quote3="Es el héroe que Gotham se merece, pero no el que necesita ahora mismo. (...) Es un guardián silencioso, un protector vigilante... Un caballero oscuro";
		String plot="X regresa para continuar su guerra contra el crimen. Con la ayuda del teniente Y y del Fiscal del Distrito Z, X se propone destruir " +
				"el crimen organizado en la ciudad de Gotham. El triunvirato demuestra su eficacia, pero, de repente, aparece un nuevo criminal que desencadena " +
				"el caos y tiene aterrados a los ciudadanos";

		
		//CLIENT
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		//JSON
		WebResource service = client.resource(getBaseURI());
		PSON pson = new PSON();
		pson.put(Movie.TITLE, title);
		pson.put(Movie.ALTERNATIVE_TITLE, alternative_title);
		pson.put(Movie.ORIGINAL_TITLE, original_title);
		pson.put(Movie.YEAR, year);
		pson.put(Movie.COUNTRY, country);
		pson.put(Movie.CONTINENT, continent);
		pson.put(Movie.DIRECTOR, director);
		pson.put(Movie.GENRE, genre);
		pson.put(Movie.PLOT, plot);
		pson.put(Movie.CAST, new String[]{actor1,actor2,actor3});
		pson.put(Movie.QUOTES, new String[]{quote1,quote2,quote3});
		pson.put(Movie.OTHERS, new String[]{other1,other2,other3});
		pson.put(Movie.CHARACTERS, new String[]{character1,character2,character3});
		pson.put(Movie.POSTER, poster);
		pson.put(Movie.POINTS, points);
		pson.put(Movie.FILMAFFINITY, filmaffinity_id);
		pson.put(Movie.IMDB, imdb_id);
		pson.put(Movie.CINEMA, cinema);
		pson.put(Movie.MASTERPIECE, masterpiece);
		pson.put(Movie.CULT, cult);
		if(action.equals(UPDATE))
			pson.put(Movie.ROWID, id);
		
		String jsss = "["+pson.toJSON()+"]";
		System.out.println("JSON sent --> "+jsss);
		Form form = new Form();
	    form.add("movies", jsss);
	    form.add("action", action);

	    //RESPONSE
	    ClientResponse  response = service.path("rest").path("movies").type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, form);
	    System.out.println("Response --> " + response.getEntity(String.class));
	    System.out.println("Response --> " + response.getStatus());
	
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
