package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.util.ApiKeyLoader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        String urlSerie = BASE_URL + nomeSerie + "&apikey=" + API_KEY;
        var json = consumoApi.obterDados(urlSerie);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            urlSerie = BASE_URL + nomeSerie + SEASON + i + "&apikey=" + API_KEY;
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
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("\nTop 5 episódios da série: "+dados.titulo());
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equals("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A) "+ e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação "+ e))
//                .limit(5)
//                .peek(e -> System.out.println("Limite "+ e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento "+ e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t
                        .episodios()
                        .stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        System.out.println("Digite um trecho do título do episódio: ");
//        var trechoDoTitulo = scan.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoDoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episódio encontrado. Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Nenhum episódio encontrado com o trecho digitado: " + trechoDoTitulo);
//        }
//
//        System.out.println("A partir de que ano deseja ver os episódios? ");
//        var ano = scan.nextInt();
//        scan.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                      "Temporada: " + e.getTemporada() +
//                              " Episodio: " + e.getTitulo() +
//                              " Data de lançamento: " + e.getDataDeLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor avaliado: " + est.getMax());
        System.out.println("Pior avaliado: " + est.getMin());
        System.out.println("Quantidade de episódios: " + est.getCount());
    }
}
