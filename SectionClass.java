/* This Class represents a Reconstruct Section. */

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.io.*;

import java.awt.image.*;
import javax.imageio.ImageIO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class SectionClass {

  String path_name = null;
  String file_name = null;
  Document section_doc = null;

  BufferedImage section_image = null;
  String image_file_names[] = new String[0];

	ArrayList<ArrayList<double[]>> strokes = new ArrayList<ArrayList<double[]>>();  // Argument (if any) specifies initial capacity (default 10)

	static void priority_println ( int thresh, String s ) {
		if (thresh >= 50) {
			System.out.println ( s );
		}
	}

  public SectionClass ( String p_name, String f_name ) {
    this.path_name = p_name;
    this.file_name = f_name;

    File section_file = new File ( this.path_name + File.separator + this.file_name );

    this.section_doc = XML_Parser.parse_xml_file_to_doc ( section_file );

    Element section_element = this.section_doc.getDocumentElement();

    priority_println ( 50, "SectionClass: This section is index " + section_element.getAttribute("index") );
    if (section_element.hasChildNodes()) {
      priority_println ( 50, "  SectionClass: This section has child nodes" );
      NodeList child_nodes = section_element.getChildNodes();
      for (int cn=0; cn<child_nodes.getLength(); cn++) {
        Node child = child_nodes.item(cn);
        if (child.getNodeName() == "Transform") {
          priority_println ( 50, "    SectionClass: Node " + cn + " is a transform" );
          String xform_dim = ((Element)child).getAttribute("dim");
          String xform_xcoef = ((Element)child).getAttribute("xcoef");
          String xform_ycoef = ((Element)child).getAttribute("ycoef");
          priority_println ( 50, "      dim = \"" + xform_dim + "\"" );
          priority_println ( 50, "      xcoef = " + xform_xcoef );
          priority_println ( 50, "      ycoef = " + xform_ycoef );
					if ( (!xform_dim.trim().equals("0")) && (!xform_dim.trim().equals("1")) )  {
						priority_println ( 100, "Transforms must be 0 or 1 dimension in this version." );
						JOptionPane.showMessageDialog(null, "SectionClass: Dim error", "SectionClass: Dim error", JOptionPane.WARNING_MESSAGE);
					}

          if (child.hasChildNodes()) {
            NodeList grandchild_nodes = child.getChildNodes();
            boolean is_image = false;
            for (int gn=0; gn<grandchild_nodes.getLength(); gn++) {
              if (grandchild_nodes.item(gn).getNodeName() == "Image") {
                is_image = true;
                break;
              }
            }
            if (is_image) {
              // This transform should contain an "Image" node and ONE "Contour" node
              for (int gn=0; gn<grandchild_nodes.getLength(); gn++) {
                Node grandchild = grandchild_nodes.item(gn);
                if (grandchild.getNodeName() == "Image") {
                  priority_println ( 40, "      SectionClass: Grandchild " + gn + " is an image" );
                  priority_println ( 40, "         SectionClass: Image name is: " + ((Element)grandchild).getAttribute("src") );
                  String new_names[] = new String[image_file_names.length + 1];
                  for (int i=0; i<image_file_names.length; i++) {
                    new_names[i] = image_file_names[i];
                  }
                  try {
                    new_names[image_file_names.length] = new File ( this.path_name + File.separator + ((Element)grandchild).getAttribute("src") ).getCanonicalPath();
                  } catch (Exception e) {
                    priority_println ( 20, "SectionClass: Error getting path for " + ((Element)grandchild).getAttribute("src") );
                    System.exit(1);
                  }
                  image_file_names = new_names;
                } else if (grandchild.getNodeName() == "Contour") {
                  priority_println ( 40, "      SectionClass: Grandchild " + gn + " is an image perimeter contour" );
                  priority_println ( 40, "         SectionClass: Contour name is: " + ((Element)grandchild).getAttribute("name") );
                }
              }
            } else {
              // This transform should contain one or more "Contour" nodes
              for (int gn=0; gn<grandchild_nodes.getLength(); gn++) {
                Node grandchild = grandchild_nodes.item(gn);
                if (grandchild.getNodeName() == "Contour") {
                  priority_println ( 40, "      SectionClass: Grandchild " + gn + " is a trace contour" );
                  priority_println ( 40, "         SectionClass: Contour name is: " + ((Element)grandchild).getAttribute("name") );
                  String points_str = ((Element)grandchild).getAttribute("points");
                  String xy_str[] = points_str.trim().split(",");
                  // Allocate an ArrayList to hold the double points
                  ArrayList<double[]> stroke = new ArrayList<double[]>(xy_str.length);
                  for (int xyi=0; xyi<xy_str.length; xyi++) {
                    String xy[] = xy_str[xyi].trim().split(" ");
                    double p[] = { (Double.parseDouble(xy[0])*165)-100, (-Double.parseDouble(xy[1])*165)+100 };
                    stroke.add ( p );
                    priority_println ( 20, "              " + xy_str[xyi].trim() + " = " + p[0] + "," + p[1] );
                  }
                  strokes.add ( stroke );
                  priority_println ( 40, "         SectionClass: Contour points: " + ((Element)grandchild).getAttribute("points") );
                }
              }
            }
          }
        }
      }
    }

    priority_println ( 50, "============== Section File " + this.file_name + " ==============" );
    // priority_println ( 50, "This section is index " + this.section_docs[i].getDocumentElement().getAttributes().getNamedItem("index").getNodeValue() );
    priority_println ( 50, "SectionClass: This section is index " + section_element.getAttribute("index") );
    priority_println ( 50, "===========================================" );

  }


  public void dump_strokes() {
    for (int i=0; i<strokes.size(); i++) {
      priority_println ( 50, " Stroke " + i );
      ArrayList<double[]> s = strokes.get(i);
	    for (int j=0; j<s.size(); j++) {
	      double p[] = s.get(j);
	      priority_println ( 50, "   Point " + j + " = [" + p[0] + "," + p[1] + "]" );
	    }
    }
  }

  public void clear_strokes() {
    strokes = new ArrayList<ArrayList<double[]>>();
  }

  public void add_stroke (	ArrayList<double[]> stroke ) {
    strokes.add ( stroke );
  }


  public BufferedImage get_image() throws OutOfMemoryError {
    if (section_image == null) {
      try {
        priority_println ( 50, " SectionClass: Opening ... " + this.image_file_names[0] );
        File image_file = new File ( this.image_file_names[0] );
        this.section_image = ImageIO.read(image_file);
      } catch (OutOfMemoryError mem_err) {
        // this.series.image_frame = null;
        priority_println ( 100, "SectionClass: **** Out of Memory Error while opening an image file:\n   " + this.image_file_names[0] );
        throw ( mem_err );
      } catch (Exception oe) {
        // this.series.image_frame = null;
        priority_println ( 100, "SectionClass: Error while opening an image file:\n   " + this.image_file_names[0] );
        JOptionPane.showMessageDialog(null, "SectionClass: File error", "SectionClass: File Path Error", JOptionPane.WARNING_MESSAGE);
      }
    }
    return ( section_image );
  }

	public boolean purge_image() {
		if (section_image == null) {
			return ( false );
		} else {
			section_image = null;
			return ( true );
		}
	}

  public void draw_stroke ( Graphics g, ArrayList<double[]> s, Reconstruct r ) {
    if (s.size() > 0) {
      int line_padding = 1;
      for (int xoffset=-line_padding; xoffset<=line_padding; xoffset++) {
        for (int yoffset=-line_padding; yoffset<=line_padding; yoffset++) {
          double p0[] = s.get(0);
          for (int j=1; j<s.size(); j++) {
            double p1[] = s.get(j);
            g.drawLine (  xoffset+r.x_to_pxi(p0[0]),   yoffset+r.y_to_pyi(p0[1]),  xoffset+r.x_to_pxi(p1[0]),  yoffset+r.y_to_pyi(p1[1]) );
            // priority_println ( 50, "   Line " + j + " = [" + p0[0] + "," + p0[1] + "] to [" + p1[0] + "," + p1[1] + "]" );
            p0 = new double[2];
            p0[0] = p1[0];
            p0[1] = p1[1];
          }
        }
      }
    }
  }


	public void paint_section (Graphics g, Reconstruct r, SeriesClass series) throws OutOfMemoryError {
	  BufferedImage image_frame = get_image();
		if (image_frame == null) {
		  priority_println ( 50, "Image is null" );
		} else {
		  // priority_println ( 50, "Image is NOT null" );
		  int img_w = image_frame.getWidth();
		  int img_h = image_frame.getHeight();
		  double img_wf = 200;
		  double img_hf = 200;
		  if (img_w >= img_h) {
		    // Make the image wider to fit
		    img_wf = img_w * img_wf / img_h;
		  } else {
		    // Make the height shorter to fit
		    img_hf = img_h * img_hf / img_w;
		  }
		  int draw_x = r.x_to_pxi(-img_wf/2.0);
		  int draw_y = r.y_to_pyi(-img_hf/2.0);
		  int draw_w = r.x_to_pxi(img_wf/2.0) - draw_x;
		  int draw_h = r.y_to_pyi(img_hf/2.0) - draw_y;
      g.drawImage ( image_frame, draw_x, draw_y, draw_w, draw_h, r );
      //g.drawImage ( image_frame, (win_w-img_w)/2, (win_h-img_h)/2, img_w, img_h, this );
    }

    g.setColor ( new Color ( 200, 0, 0 ) );
    for (int i=0; i<strokes.size(); i++) {
      // priority_println ( 50, " Stroke " + i );
      ArrayList<double[]> s = strokes.get(i);
      draw_stroke ( g, s, r );
    }
    if (r.stroke != null) {
      g.setColor ( new Color ( 255, 0, 0 ) );
      draw_stroke ( g, r.stroke, r );
    }
    if (r.center_draw) {
      g.setColor ( new Color ( 255, 255, 255 ) );
      int cx = r.getSize().width / 2;
      int cy = r.getSize().height / 2;
      g.drawLine ( cx-10, cy, cx+10, cy );
      g.drawLine ( cx, cy-10, cx, cy+10 );
    }
	}




	public static void main ( String[] args ) {
		priority_println ( 50, "Testing SectionClass.java ..." );
		File sf = new File ("data/organelle_series/organelle_3_slice.ser");
		SeriesClass sc = new SeriesClass(sf);
		Element sec0 = sc.section_docs[0].getDocumentElement();
		priority_println ( 50, "NodeName = " + sec0.getNodeName() );
		priority_println ( 50, "thickness = " + sec0.getAttribute("thickness") );
		priority_println ( 50, "index = " + sec0.getAttribute("index") );
		String sfnames[] = sc.get_section_file_names(sf.getParent(), sf.getName().substring(0,sf.getName().length()-4));
		priority_println ( 50, "sfnames:" );
		for (int i=0; i<sfnames.length; i++) {
			priority_println ( 50, "  " + sfnames[i] );
		}
		Element se = sc.section_docs[0].getDocumentElement();
	}

}

