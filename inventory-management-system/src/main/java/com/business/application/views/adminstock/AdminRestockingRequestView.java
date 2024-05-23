package com.business.application.views.adminstock;
import java.math.BigDecimal;
import com.business.application.components.avataritem.AvatarItem;
import com.business.application.data.SamplePerson;
import com.business.application.services.SamplePersonService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.business.application.data.Product;
import com.business.application.services.ProductService;
import java.util.ArrayList;

@PageTitle("Admin Restocking Request")
@Route(value = "admin-restock-request", layout = MainLayout.class)
// @RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class AdminRestockingRequestView extends Composite<VerticalLayout> {
   
    private Grid<Product> grid1;

    public AdminRestockingRequestView() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);
        VerticalLayout layoutColumn2 = new VerticalLayout();
        layoutColumn2.getStyle().set("flex-grow", "1");
        
        grid1 = new Grid<Product>(Product.class, false);
        grid1.addColumn("productId").setAutoWidth(true);
        grid1.addColumn("name").setAutoWidth(true);
        grid1.addColumn("salePrice").setAutoWidth(true);
        grid1.addColumn("category").setAutoWidth(true);
        grid1.addColumn("description").setAutoWidth(true);
        setProductGridSampleData(grid1);
        layoutColumn2.add(grid1);

        VerticalLayout layoutColumn1 = new VerticalLayout();
        layoutColumn1.getStyle().set("flex-grow", "1");
        MultiSelectListBox avatarItems = new MultiSelectListBox();
        avatarItems.setWidth("min-content");
        avatarItems.getStyle().set("flex-grow", "1");
        setAvatarItemsSampleData(avatarItems);
        layoutColumn1.add(avatarItems);
        mainLayout.add(layoutColumn1);
        mainLayout.add(layoutColumn2);
        getContent().add(mainLayout);
    }

    private void setAvatarItemsSampleData(MultiSelectListBox multiSelectListBox) {
        record Person(String name, String profession) {
        }
        ;
        List<Person> data = List.of(new Person("Aria Bailey", "Endocrinologist"), new Person("Aaliyah Butler", "Nephrologist"), new Person("Eleanor Price", "Ophthalmologist"), new Person("Allison Torres", "Allergist"), new Person("Madeline Lewis", "Gastroenterologist"));
        multiSelectListBox.setItems(data);
        multiSelectListBox.setRenderer(new ComponentRenderer(item -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(((Person) item).name);
            avatarItem.setDescription(((Person) item).profession);
            avatarItem.setAvatar(new Avatar(((Person) item).name));
            return avatarItem;
        }));
    }

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

     private void setProductGridSampleData(Grid<Product> grid) {
        // Create sample products
        List<Product> sampleProducts = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(1902L);
        product1.setName("Sample Product 1");
        product1.setSalePrice(new BigDecimal("10.00"));
        product1.setCategory("Category 1");
        product1.setDescription("This is a sample product 1");
        sampleProducts.add(product1);

        // Set the sample products to the grid
        grid.setItems(sampleProducts);
    }

      private void createProduct(String name, BigDecimal salePrice, String category, String description) {
        // Create a new product instance
        Product product = new Product();
        product.setName(name);
        product.setSalePrice(salePrice);
        product.setCategory(category);
        product.setDescription(description);
    }

    @Autowired()
    private SamplePersonService samplePersonService;

     @Autowired()
    private ProductService productService;
}
