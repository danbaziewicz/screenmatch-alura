package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.util.ApiKeyLoader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private ConverteDados conversor = new ConverteDados();
    private ConsumoApi consumoApi = new ConsumoApi();
    private Scanner scan = new Scanner(System.in);
    private static final String API_KEY = ApiKeyLoader.loadApiKey();
    private static final String BASE_URL = "https://www.omdbapi.com/?t=";
    private static final String SEASON = "&season=";


    public void exibeMenu() {
        System.out.println("Digite o nome da série que deseja pesquisar: ");
        String nomeSerie = URLEncoder.encode(scan.nextLine(), StandardCharsets.UTF_8);
        String urlSerie = BASE_URL+nomeSerie+API_KEY;
        var json = consumoApi.obterDados(urlSerie);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i<=dados.totalTemporadas(); i++) {
			urlSerie = BASE_URL+nomeSerie+SEASON+i+API_KEY;
			json = consumoApi.obterDados(urlSerie);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

//        for(int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
