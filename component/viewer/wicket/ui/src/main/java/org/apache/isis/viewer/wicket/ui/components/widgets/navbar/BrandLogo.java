package org.apache.isis.viewer.wicket.ui.components.widgets.navbar;

import com.google.inject.name.Named;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;

/**
 * A component used as a brand logo in the top-left corner of the navigation bar
 */
public class BrandLogo extends WebComponent {


    @com.google.inject.Inject(optional = true)
    @Named("brandLogo")
    private String logoUrl;

    /**
     * Constructor.
     *
     * @param id The component id
     */
    public BrandLogo(final String id) {
        super(id);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("src", logoUrl);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(logoUrl != null);
    }
}
