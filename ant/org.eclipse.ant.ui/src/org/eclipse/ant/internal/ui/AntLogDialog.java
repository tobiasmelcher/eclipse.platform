package org.eclipse.ant.internal.ui;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
 
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AntLogDialog extends Dialog {

	private TextViewer viewer;
	private String contents;

	// constants
	private final static int SIZING_VIEWER_HEIGHT = 300;
	private final static int SIZING_VIEWER_WIDTH = 300;

	public AntLogDialog(Shell parent,String contents) {
		super(parent);
		this.contents = contents;
		//...setText("Console");
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(
			GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		// SWT.WRAP doesn't work ??
		viewer = new TextViewer(composite,SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = SIZING_VIEWER_HEIGHT;
		data.widthHint = SIZING_VIEWER_WIDTH;
		viewer.setEditable(false);
		viewer.getControl().setLayoutData(data);
		viewer.setDocument(new Document(contents));

		return composite;
	}
	
}