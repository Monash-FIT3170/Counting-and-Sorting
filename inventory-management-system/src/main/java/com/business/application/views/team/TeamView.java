package com.business.application.views.team;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

@PageTitle("Team")
@Route(value = "data-grid3", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TeamView extends Div {

    private GridPro<Member> grid;
    private GridListDataView<Member> gridListDataView;

    private Grid.Column<Member> memberColumn;
    private Grid.Column<Member> storeColumn;
    private Grid.Column<Member> emailColumn;
    private final TextField name = new TextField("");

    public TeamView() {
        addClassName("suppliers-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFilters();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        grid.setWidth("80%");
        

        List<Member> members = getMembers();
        gridListDataView = grid.setItems(members);
    }

    private void addColumnsToGrid() {
        createMemberColumn();
        createStatusColumn();
        createDateColumn();
    }

    private void createMemberColumn() {
        memberColumn = grid.addColumn(new ComponentRenderer<>(member -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(member.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(member.getName());
            hl.add(img, span);
            return hl;
        })).setComparator(member -> member.getName()).setHeader("Name");
    }

    private void createStatusColumn() {
        storeColumn = grid.addColumn(new ComponentRenderer<>(member -> {
                HorizontalLayout hl = new HorizontalLayout();
                hl.setAlignItems(Alignment.CENTER);
                Span span = new Span();
                span.setClassName("store");
                span.setText(member.getStore());
                hl.add(span);
                return hl;
        })).setComparator(member -> member.getStore()).setHeader("Store");
    }

    private void createDateColumn() {
        storeColumn = grid.addColumn(new ComponentRenderer<>(member -> {
                HorizontalLayout hl = new HorizontalLayout();
                hl.setAlignItems(Alignment.CENTER);
                Span span = new Span();
                span.setClassName("email");
                span.setText(member.getEmail());
                hl.add(span);
                return hl;
        })).setComparator(member -> member.getEmail()).setHeader("Email");
    }

    private void addFilters() {
        addClassName("filter-layout");
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BoxSizing.BORDER);
        name.setPlaceholder("Search Members");
        add(name);
    }

    private boolean areStatusesEqual(Member member, ComboBox<String> statusFilter) {
        String statusFilterValue = statusFilter.getValue();
        if (statusFilterValue != null) {
            return StringUtils.equals(member.getStore(), statusFilterValue);
        }
        return true;
    }

    private boolean areDatesEqual(Member member, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate memberDate = LocalDate.parse(member.getEmail());
            return dateFilterValue.equals(memberDate);
        }
        return true;
    }

    private List<Member> getMembers() {
        return Arrays.asList(
                createMember(4957, "https://randomuser.me/api/portraits/women/42.jpg", "Amarachi Nkechi",
                        "Clayton", "Henry@countingsorting.com"),
                createMember(675, "https://randomuser.me/api/portraits/women/24.jpg", "Bonelwa Ngqawana",
                        "Clayton", "Henry@countingsorting.com"),
                createMember(6816, "https://randomuser.me/api/portraits/men/42.jpg", "Debashis Bhuiyan",
                        "Clayton", "Henry@countingsorting.com"),
                createMember(5144, "https://randomuser.me/api/portraits/women/76.jpg", "Jacqueline Asong",
                        "Peninsula", "Henry@countingsorting.com"),
                createMember(9800, "https://randomuser.me/api/portraits/men/24.jpg", "Kobus van de Vegte",
                        "Peninsula", "Henry@countingsorting.com"),
                createMember(3599, "https://randomuser.me/api/portraits/women/94.jpg", "Mattie Blooman",
                        "Caulfield", "Henry@countingsorting.com"),
                createMember(3989, "https://randomuser.me/api/portraits/men/76.jpg", "Oea Romana", "Peninsula",
                        "Henry@countingsorting.com"),
                createMember(1077, "https://randomuser.me/api/portraits/men/94.jpg", "Stephanus Huggins",
                        "Clayton", "Henry@countingsorting.com"),
                createMember(8942, "https://randomuser.me/api/portraits/men/16.jpg", "Torsten Paulsson",
                        "Peninsula", "Henry@countingsorting.com"));
    }

    private Member createMember(int id, String img, String member, String store, String email) {
        Member c = new Member();
        c.setId(id);
        c.setImg(img);
        c.setName(member);
        c.setStore(store);
        c.setEmail(email);

        return c;
    }
};
