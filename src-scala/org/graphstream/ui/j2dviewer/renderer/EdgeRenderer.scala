/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.j2dviewer.renderer

import java.awt.Graphics2D
import org.graphstream.ui.graphicGraph.{GraphicElement, StyleGroup, GraphicEdge}
import org.graphstream.ui.j2dviewer.J2DGraphRenderer
import org.graphstream.ui.j2dviewer.{Camera, Backend}
import org.graphstream.ui.j2dviewer.renderer.shape._

class EdgeRenderer(styleGroup:StyleGroup) extends StyleRenderer(styleGroup) {

	var shape:Shape = null
	var arrow:AreaOnConnectorShape = null
  
	protected def setupRenderingPass(bck:Backend, camera:Camera, forShadow:Boolean) {
		shape = bck.chooseEdgeShape(shape, group)
		arrow = bck.chooseEdgeArrowShape(arrow, group).asInstanceOf[AreaOnConnectorShape]
	}
	
	protected def pushStyle(bck:Backend, camera:Camera, forShadow:Boolean) {
	  	shape.configureForGroup(bck, group, camera)
		
		if(arrow ne null) {
			arrow.configureForGroup(bck, group, camera)
		}
	}
	
	protected def pushDynStyle(bck:Backend, camera:Camera, element:GraphicElement) {
	}
	
	protected def renderElement(bck:Backend, camera:Camera, element:GraphicElement) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo(element)
		
		shape.configureForElement(bck, element, info, camera)
		shape.render(bck, camera, element, info)
  
		if(edge.isDirected && (arrow ne null)) {
			arrow.theConnectorYoureAttachedTo(shape.asInstanceOf[Connector] /* !!!! Test this TODO ensure this !!! */)
			arrow.configureForElement(bck, element, info, camera)
		  	arrow.render(bck, camera, element, info)
		}
	}
	
	protected def renderShadow(bck:Backend, camera:Camera, element:GraphicElement) {
		val edge = element.asInstanceOf[GraphicEdge]
		val info = getOrSetEdgeInfo(element)
		
		shape.configureForElement(bck, element, info, camera)
		shape.renderShadow(bck, camera, element, info)
  
		if(edge.isDirected && (arrow ne null)) {
			arrow.theConnectorYoureAttachedTo(shape.asInstanceOf[Connector] /* !!!! Test this TODO ensure this !!! */)
			arrow.configureForElement(bck, element, info, camera)
			arrow.renderShadow(bck, camera, element, info)
		}
	}
	
	/** Retrieve the shared edge informations stored on the given edge element.
	 * If such information is not yet present, add it to the element. 
	 * @param element The element to look for.
	 * @return The edge information.
	 * @throws RuntimeException if the element is not an edge.
	 */
	protected def getOrSetEdgeInfo(element:GraphicElement):EdgeInfo= {
		if(element.isInstanceOf[GraphicEdge]) {
			var info = element.getAttribute(ElementInfo.attributeName).asInstanceOf[EdgeInfo]
			
			if(info eq null) {
				info = new EdgeInfo
				element.setAttribute(ElementInfo.attributeName, info)
			}
			
			info
		} else {
			throw new RuntimeException("Trying to get EdgeInfo on non-edge...")
		}
	}
 
	protected def elementInvisible(bck:Backend, camera:Camera, element:GraphicElement) {
	  
	}
}

object EdgeRenderer {
	def apply( style:StyleGroup, mainRenderer:J2DGraphRenderer ):StyleRenderer = {
		new EdgeRenderer( style )
	}
}