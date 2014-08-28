/**
 * JFX4SWT - JavaFX library adapter for SWT framework.
 *
 * Copyright (c) 2014 Alexey Aksenov ezh@ezh.msk.ru
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digimead.digi.lib.jfx4swt

import com.sun.glass.ui.Screen
import java.lang.reflect.InvocationTargetException
import java.util.Locale
import java.util.concurrent.{ Exchanger, TimeUnit }
import org.digimead.digi.lib.api.XDependencyInjection
import org.digimead.digi.lib.jfx4swt.api.XApplication
import org.digimead.digi.lib.jfx4swt.jfx.{ FXAdapter, FXHost, JFaceCanvas }
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Display
import scala.language.implicitConversions

/**
 * JFX4SWT application adapter.
 */
abstract class JFXApplication extends com.sun.glass.ui.Application with XApplication {
  /** Create embedded. */
  def createJFaceCanvas(host: FXHost): JFaceCanvas
  /** Create host. */
  def createHost(adapter: FXAdapter): FXHost
}

object JFXApplication {
  /** Application virtual screen. */
  lazy val virtualScreen = DI.virtualScreenBuilder()

  /**
   * Detect the operating system from the os.name System property and cache
   * the result
   *
   * @return the operating system detected
   */
  def getOperatingSystemType() = {
    val OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH)
    if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0))
      "MacOS"
    else if (OS.indexOf("win") >= 0)
      "Windows"
    else if (OS.indexOf("nux") >= 0)
      "Linux"
    else
      "Other"
  }

  /** Build virtual screen. */
  class VirtualScreenBuilder extends Function0[Screen] {
    def apply() = try {
      val screenConstructor = classOf[Screen].getDeclaredConstructor(
        java.lang.Long.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE,
        java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE,
        java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE,
        java.lang.Float.TYPE)
      if (!screenConstructor.isAccessible())
        screenConstructor.setAccessible(true)
      val display = Display.getDefault()
      val exchanger = new Exchanger[(Int, Point)]()
      display.asyncExec(new Runnable {
        def run = exchanger.exchange((Display.getDefault().getDepth(), Display.getDefault().getDPI()),
          100, TimeUnit.MILLISECONDS)
      })
      val (depth, dpi) = exchanger.exchange(null)
      val nativePtr: java.lang.Long = 1
      val x: java.lang.Integer = 0
      val y: java.lang.Integer = 0
      val width: java.lang.Integer = 4096
      val height: java.lang.Integer = 4096
      val visibleX: java.lang.Integer = 0
      val visibleY: java.lang.Integer = 0
      val visibleWidth: java.lang.Integer = 4096
      val visibleHeight: java.lang.Integer = 4096
      val resolutionX: java.lang.Integer = dpi.x
      val resolutionY: java.lang.Integer = dpi.y
      val scale: java.lang.Float = 1
      screenConstructor.newInstance(nativePtr, depth: java.lang.Integer, x, y, width, height,
        visibleX, visibleY, visibleWidth, visibleHeight, resolutionX, resolutionY, scale)
    } catch {
      case e: NoSuchMethodException ⇒ throw new RuntimeException("Unable to construct a Screen", e);
      case e: InvocationTargetException ⇒ throw new RuntimeException("Unable to construct a Screen", e);
      case e: InstantiationException ⇒ throw new RuntimeException("Unable to construct a Screen", e);
      case e: IllegalAccessException ⇒ throw new RuntimeException("Unable to construct a Screen", e);
    }
  }
  /**
   * Dependency injection routines.
   */
  private object DI extends XDependencyInjection.PersistentInjectable {
    /** JFX virtual screen builder. */
    lazy val virtualScreenBuilder = injectOptional[VirtualScreenBuilder] getOrElse new VirtualScreenBuilder
  }
}
