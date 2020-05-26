package pl.coderslab.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

import static pl.coderslab.service.HateoasUtil.Relation;
import static pl.coderslab.service.HateoasUtil.createLinkHeader;

@Component
public class paginatedResultsRetrievedListener implements ApplicationListener<PaginatedResultsRetrievedEvent> {

    @Override
    public void onApplicationEvent(PaginatedResultsRetrievedEvent event) {
        addPagedResourceNavigationLinksIntoHeader(event.getUriBuilder(), event.getClazz(), event.getResponse(), event.getApiEndpointPrefix(), event.getPage(), event.getTotalPages(), event.getSize());
    }

    void addPagedResourceNavigationLinksIntoHeader(UriComponentsBuilder uriBuilder, Class clazz,
                                                   HttpServletResponse response, String apiEndpointPrefix,
                                                   int page, int totalPages, int size) {
        setResourceInPath(uriBuilder, clazz, apiEndpointPrefix);
        StringJoiner linkHeader = new StringJoiner(", ");

        if (hasNextPage(page, totalPages)) {
            String uriForNextPage = constructNextPageUri(uriBuilder, page, size);
            linkHeader.add(createLinkHeader(uriForNextPage, Relation.REL_NEXT));
        }

        if (hasPreviousPage(page)) {
            String uriForPreviousPage = constructPreviousPageUri(uriBuilder, page, size);
            linkHeader.add(createLinkHeader(uriForPreviousPage, Relation.REL_PREV));
        }

        if (hasFirstPage(page)) {
            String uriForFirstPage = constructFirstPageUri(uriBuilder, size);
            linkHeader.add(createLinkHeader(uriForFirstPage, Relation.REL_FIRST));
        }

        if (hasLastPage(page, totalPages)) {
            String uriForLastPage = constructLastPageUri(uriBuilder, totalPages - 1, size);
            linkHeader.add(createLinkHeader(uriForLastPage, Relation.REL_LAST));
        }

        if (linkHeader.length() > 0) {
            response.addHeader("Link", linkHeader.toString());
            response.addHeader("Current-Page", String.valueOf(page));
            response.addHeader("Total-Pages", String.valueOf(totalPages));
    }
}

    private String constructNextPageUri(UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder.replaceQueryParam("page", page +1)
                .replaceQueryParam("size", size)
                .build().encode().toUriString();
    }

    private String constructPreviousPageUri(UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder.replaceQueryParam("page", page - 1)
                .replaceQueryParam("size", size)
                .build().encode().toUriString();
    }

    private String constructFirstPageUri(UriComponentsBuilder uriBuilder, int size) {
        return uriBuilder.replaceQueryParam("page", 0)
                         .replaceQueryParam("size", size)
                         .build().encode().toUriString();
    }

    private String constructLastPageUri(UriComponentsBuilder uriBuilder, int totalPages, int size) {
        return uriBuilder.replaceQueryParam("page", totalPages)
                         .replaceQueryParam("size", size)
                         .build().encode().toUriString();
    }

    private boolean hasNextPage(int page, int totalPages) {
        return page < (totalPages - 1);
    }

    private boolean hasPreviousPage(int page) {
        return page > 0;
    }

    private boolean hasFirstPage(int page) {
        return hasPreviousPage(page);
    }

    private boolean hasLastPage(int page, int totalPages) {
        return (totalPages > 1) && hasNextPage(page, totalPages);
    }


    private void setResourceInPath(UriComponentsBuilder uriBuilder, Class clazz, String apiEndpointPrefix) {
        String resourceName = clazz.getSimpleName().toLowerCase() + "s";
        uriBuilder.path(apiEndpointPrefix + resourceName);
    }
}
