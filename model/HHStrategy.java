package com.javarush.task.task28.task2810.model;

import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";

    protected Document getDocument(String searchString, int page) throws IOException {
        String format = String.format(URL_FORMAT, searchString, page);

        Document document = Jsoup.connect(format)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                .referrer("none")
                .get();

        return document;
    }

    @Override
    public List<Vacancy> getVacancies(String searchString) {

        List<Vacancy> vacancyList = new ArrayList<>();

        try {
            int pageNumber = 0;
            Document document;

            while (true) {
                document = getDocument(searchString, pageNumber++);
                if (document == null) break;
                Elements elements = document.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.size() == 0) break;

                for (Element e : elements) {
                    Element titleElement = e.select("[data-qa=vacancy-serp__vacancy-title]").first();
                    String title = titleElement.text();

                    Element salaryElement = e.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    String salary = salaryElement == null ? "" : salaryElement.text();

                    String city = e.select("[data-qa=vacancy-serp__vacancy-address]").first().text();

                    String company = e.select("[data-qa=vacancy-serp__vacancy-employer]").first().text();

                    String siteName = "http://hh.ua/";

                    String url = titleElement.attr("href");

                    Vacancy vacancy = new Vacancy();
                    vacancy.setCity(city);
                    vacancy.setCompanyName(company);
                    vacancy.setSalary(salary);
                    vacancy.setTitle(title);
                    vacancy.setSiteName(siteName);
                    vacancy.setUrl(url);

                    vacancyList.add(vacancy);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vacancyList;
    }
}
