package com.persistent.genai.ui;

import org.springframework.util.StringUtils;

import com.persistent.genai.model.Product;
import com.persistent.genai.repository.ProductRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;


@Route()
public class MainView extends VerticalLayout {

	private final ProductRepository repo;

	private final ProductEditor editor;
	
	public final Grid<Product> grid;
	
	final TextField filter;

	private final Button addNewBtn;

	public MainView(ProductRepository repo, ProductEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Product.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("Add Product to cart", VaadinIcon.PLUS.create());

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		add(actions, grid, editor);
		
		grid.setHeight("300px");
		grid.setColumns("id", "productName", "description", "price", "quantity");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
		
		filter.setPlaceholder("Filter by product name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listProducts(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editProduct(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editProduct(new Product("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listProducts(filter.getValue());
		});

		// Initialize listing
		listProducts(null);
	}

	// tag::listCustomers[]
	public void listProducts(String filterText) {
		if (StringUtils.hasText(filterText)) {
			grid.setItems(repo.findByProductNameStartsWithIgnoreCase(filterText));
		} else {
			grid.setItems(repo.findAll());
		}
	}
	// end::listCustomers[]

}
