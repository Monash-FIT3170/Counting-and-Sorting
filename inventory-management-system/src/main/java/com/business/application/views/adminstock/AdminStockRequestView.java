package com.business.application.views.adminstock;
import com.business.application.data.Product;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Admin Restock Requests")
@Route(value = "restock-requests", layout = MainLayout.class)
@AnonymousAllowed
public class AdminRestockRequestView extends Div {

    private Grid<Product> grid;
    private final ProductService productService;

    @Autowired
    public AdminRestockRequestView(ProductService productService) {
        this.productService = productService;
        setSizeFull();
        addClassNames("admin-restock-request-view");

        VerticalLayout layout = new VerticalLayout(createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Component createGrid() {
        grid = new Grid<>(Product.class, false);
        grid.addColumn("name").setHeader("Product Name").setAutoWidth(true);
        grid.addColumn("category").setHeader("Category").setAutoWidth(true);
        grid.addColumn("salePrice").setHeader("Sale Price").setAutoWidth(true);
        grid.addColumn("description").setHeader("Description").setAutoWidth(true);

        grid.setItems(productService.getAllProducts());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }
}
