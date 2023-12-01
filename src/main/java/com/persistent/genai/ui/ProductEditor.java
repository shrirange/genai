package com.persistent.genai.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.persistent.genai.model.Product;
import com.persistent.genai.repository.ProductRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in MainView.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX.
 */
@SpringComponent
@UIScope
public class ProductEditor extends VerticalLayout implements KeyNotifier {

	private final ProductRepository repository;

	/**
	 * The currently edited customer
	 */
	private Product product;

	/* Fields to edit properties in Customer entity */
	public TextField productName = new TextField("Product Name");

	public TextField description= new TextField("Description");
	
	public BigDecimalField  price = new BigDecimalField("Price");

	public IntegerField  quantity = new IntegerField("Quantity");
	

	/* Action buttons */
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Product> binder = new Binder<>(Product.class);
	private ChangeHandler changeHandler;

	@Autowired
	public ProductEditor(ProductRepository repository) {
		this.repository = repository;

		add(productName, description, price, quantity, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editProduct(product));
		setVisible(false);
	}

	public void delete() {
		repository.delete(product);
		changeHandler.onChange();
	}

	public void save() {
		repository.save(product);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}

	public final void editProduct(Product p) {
		if (p == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = p.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			// In a more complex app, you might want to load
			// the entity/DTO with lazy loaded relations for editing
			product = repository.findById(p.getId()).get();
		}
		else {
			product = p;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(product);

		setVisible(true);

		// Focus first name initially
		productName.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}

}