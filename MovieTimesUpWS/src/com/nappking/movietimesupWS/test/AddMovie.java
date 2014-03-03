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
		int 	id = 19;
		int 	points = 1;
		int 	year = 1976;
		String  action = SAVE;
		String 	title = "Novecento";
		String 	alternative_title = "1900";
		String 	original_title = "Novecento (1900)";
		String 	country = "Europa";
		String 	continent = "Italia";
		String 	director = "Bernardo Bertolucci";
		String 	genre = "Drama";
		String 	poster = "";
		String  filmaffinity_id = "903598";
		String	imdb_id = "tt0074084";
		String 	actor1="Gérard Depardieu";
		String 	actor2="Robert De Niro";
		String 	actor3="Dominique Sanda";
		String 	character1="Alfredo Berlinghieri";
		String 	character2="Olmo Dalcò";
		String 	character3="Ada Fiastri";
		String 	other1 ="El actor principal siempre se ha arrepentido de rodar la escena en la " +
				"que aparece desnudo (junto al actor secundario) y es masturbado por una prostituta";
		String 	other2 ="Una obra maestra imborrable, la descripción sociopolítica de la primera mitad del s.XX europeo";
		String 	other3 ="Su duración es superior a 5 horas";
		String 	quote1="-…Dime, pequeño guerrero, ¿qué opinas de tu patrón? -Ya no hay patrón";
		String 	quote2="Han nacido en el campo, ¡oh, burla del destino! El hijo del amo y el bastardo campesino.";
		String 	quote3="Los fascistas no son como los hongos, que nacen así en una noche, no. Han sido los patrones " +
				"los que han plantado los fascistas, los han querido, les han pagado.";
		String 	plot = "En el año 1901, en una finca del norte de Italia, nacen el mismo día el hijo " +
				"de un terrateniente y el hijo de un bracero que serán amigos inseparables, aunque su " +
				"relación se verá nublada por sus diferentes actitudes frente al fascismo. Drama que hace " +
				"un complejo recorrido político y social por la Italia del siglo XX.";
		
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
	
	}

	private static URI getBaseURI() {
		//return UriBuilder.fromUri("http://localhost:8081/MovieTimesUpWS").build();
		return UriBuilder.fromUri("http://movietimesup.gestores.cloudbees.net").build();
	}


}
