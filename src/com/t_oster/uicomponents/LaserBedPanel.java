/**
 * This file is part of VisiCut.
 * Copyright (C) 2011 - 2013 Thomas Oster <thomas.oster@rwth-aachen.de>
 * RWTH Aachen University - 52062 Aachen, Germany
 * Copyright (C) 2016 Jürgen Weigert <juewei@fabfolk.com>
 *
 *     VisiCut is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     VisiCut is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.t_oster.uicomponents;

import com.t_oster.liblasercut.platform.Util;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 * @author Jürgen Weigert <juewei@fabfolk.com>
 */
public class LaserBedPanel extends javax.swing.JPanel implements PropertyChangeListener
{

  /**
   * Creates new form LaserBedPanel
   */
  public LaserBedPanel()
  {
    initComponents();
    tfFocus.addPropertyChangeListener(UnitTextfield.PROP_VALUE, this);
    tfPower.addPropertyChangeListener(UnitTextfield.PROP_VALUE, this);
  }

  private boolean ignoreTextfieldUpdates = false;
  public void propertyChange(PropertyChangeEvent pce)
  {
    System.out.println("LaserBedPanel propertyChangeEvent seen:" + pce);
    if (!ignoreTextfieldUpdates)
    {
      Rectangle2D oldRectangle = this.rectangle;
      this.rectangle = this.getRectangleFromTextfields();
      firePropertyChange(PROP_RECTANGLE, oldRectangle, this.rectangle);
    }
  }

  private void updateRectanlgeText()
  {
    this.ignoreTextfieldUpdates = true;
    // tf_Width.setValue(this.rectangle.getWidth());
    updateXYText();
    this.ignoreTextfieldUpdates = false;
  }


  private double[] focus_3x3 = new double[]{ -1, 0, 1, 1, 2, 3, 3, 4, 5 };
  private double[] power_3x3 = new double[]{ .10, .11, .12, .13, .14, .15, .16, .17, .18 };

  private Rectangle2D rectangle = null;
  public static final String PROP_RECTANGLE = "rectangle";

  /**
   * Get the value of rectangle
   *
   * @return the value of rectangle
   */
  public Rectangle2D getRectangle()
  {
    return rectangle;
  }

  private void updateXYText()
  {
    AncorPointPanel.Position where = this.ancorPointPanelBed.getPosition();
    System.out.println("LaserBedPanel updateXYText seen: position " + where);
    double f,p;
    switch (where)
      {
	case TOP_LEFT:
	  f = this.focus_3x3[0];
	  p = this.power_3x3[0];
	  break;
	case TOP_CENTER:
	  f = this.focus_3x3[1];
	  p = this.power_3x3[1];
	  break;
	case TOP_RIGHT:
	  f = this.focus_3x3[2];
	  p = this.power_3x3[2];
	  break;
	case CENTER_LEFT:
	  f = this.focus_3x3[3];
	  p = this.power_3x3[3];
	  break;
	case CENTER_CENTER:
	  f = this.focus_3x3[4];
	  p = this.power_3x3[4];
	  break;
	case CENTER_RIGHT:
	  f = this.focus_3x3[5];
	  p = this.power_3x3[5];
	  break;
	case BOTTOM_LEFT:
	  f = this.focus_3x3[6];
	  p = this.power_3x3[6];
	  break;
	case BOTTOM_CENTER:
	  f = this.focus_3x3[7];
	  p = this.power_3x3[7];
	  break;
	case BOTTOM_RIGHT:
	  f = this.focus_3x3[8];
	  p = this.power_3x3[8];
	  break;
	default:
	  f = 0;
	  p = 0;
	  break;
      }
    boolean oldIgnoreTextfieldUpdates = ignoreTextfieldUpdates;
    ignoreTextfieldUpdates = true;
    tfFocus.setValue(checkNaN(f));
    tfPower.setValue(checkNaN(p));
    ignoreTextfieldUpdates = oldIgnoreTextfieldUpdates;
  }

  /**
   * Checks d if it's = NaN and
   * returns 0 in this case, else d
   * @param d
   * @return d or 0 if d == NaN
   */
  private double checkNaN(double d)
  {
    return Double.isNaN(d) ? 0 : d;
  }
  
  private Rectangle2D getRectangleFromTextfields()
  {
    try
    {
      double w = 0;	// dummy tf_Width.getValue();
      double h = 0;	// dummy
      double x = tfFocus.getValue();
      double y = tfPower.getValue();
      switch (this.ancorPointPanelBed.getPosition())
      {
        case TOP_LEFT:
          break;
        case TOP_CENTER:
          x -= w/2;
          break;
        case TOP_RIGHT:
          x -= w;
          break;
        case CENTER_LEFT:
          y -= h/2;
          break;
        case CENTER_CENTER:
          y -= h/2;
          x -= w/2;
          break;
        case CENTER_RIGHT:
          y -= h/2;
          x -= w;
          break;
        case BOTTOM_LEFT:
          y -= h;
          break;
        case BOTTOM_CENTER:
          y -= h;
          x -= w/2;
          break;
        case BOTTOM_RIGHT:
          y -= h;
          x -= w;
          break;
        default:
          x = 0;
          y = 0;
      }
      return new Rectangle2D.Double(x,y,w,h);
    }
    catch (NumberFormatException e)
    {
      return this.rectangle;
    }
  }
  /**
   * Set the value of rectangle
   *
   * @param rectangle new value of rectangle
   */
  public void setRectangle(Rectangle2D rectangle)
  {
    Rectangle2D oldRectangle = this.rectangle;
    if (Util.differ(oldRectangle, rectangle))
    {
      this.rectangle = new Rectangle2D.Double(checkNaN(rectangle.getX()), checkNaN(rectangle.getY()), checkNaN(rectangle.getWidth()), checkNaN(rectangle.getHeight()));
      this.updateRectanlgeText();
      firePropertyChange(PROP_RECTANGLE, oldRectangle, this.rectangle);
    }
  }


  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    jPanelBed = new javax.swing.JPanel();
    lbFocus = new java.awt.Label();
    lbPower = new java.awt.Label();
    ancorPointPanelBed = new com.t_oster.uicomponents.AncorPointPanel();
    tfFocus = new com.t_oster.uicomponents.LengthTextfield();
    tfPower = new com.t_oster.uicomponents.PercentTextfield();

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/t_oster/uicomponents/resources/LaserBedPanel"); // NOI18N



    jPanelBed.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("REFERENCE"))); // NOI18N

    lbFocus.setText(bundle.getString("FOCUS")); // NOI18N

    lbPower.setText(bundle.getString("POWER")); // NOI18N

    ancorPointPanelBed.addPropertyChangeListener(new java.beans.PropertyChangeListener()
    {
      public void propertyChange(java.beans.PropertyChangeEvent evt)
      {
        ancorPointPanelBedPropertyChange(evt);
      }
    });

    javax.swing.GroupLayout jPanelBedLayout = new javax.swing.GroupLayout(jPanelBed);
    jPanelBed.setLayout(jPanelBedLayout);
    jPanelBedLayout.setHorizontalGroup(
      jPanelBedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanelBedLayout.createSequentialGroup()
        .addComponent(ancorPointPanelBed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanelBedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanelBedLayout.createSequentialGroup()
            .addComponent(lbFocus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tfFocus, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
          .addGroup(jPanelBedLayout.createSequentialGroup()
            .addComponent(lbPower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tfPower, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanelBedLayout.setVerticalGroup(
      jPanelBedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanelBedLayout.createSequentialGroup()
        .addComponent(ancorPointPanelBed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 21, Short.MAX_VALUE))
      .addGroup(jPanelBedLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanelBedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(lbFocus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanelBedLayout.createSequentialGroup()
            .addComponent(tfFocus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanelBedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(lbPower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanelBedLayout.createSequentialGroup()
            .addGap(1, 1, 1)
            .addComponent(tfPower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanelBed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
			)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
		))))
        .addContainerGap(25, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanelBed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    	)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    	))
          .addGroup(layout.createSequentialGroup()
            .addGap(23, 23, 23)
	    ))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		)
        .addContainerGap(74, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void ancorPointPanelBedPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_ancorPointPanelBedPropertyChange
  {//GEN-HEADEREND:event_ancorPointPanelBedPropertyChange
    this.updateXYText();
  }//GEN-LAST:event_ancorPointPanelBedPropertyChange

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.t_oster.uicomponents.AncorPointPanel ancorPointPanelBed;
  private javax.swing.JPanel jPanelBed;
  private java.awt.Label lbFocus;
  private java.awt.Label lbPower;
  private com.t_oster.uicomponents.LengthTextfield tfFocus;
  private com.t_oster.uicomponents.PercentTextfield tfPower;
  // End of variables declaration//GEN-END:variables

}
