package gje.gquarter.toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Rect2i {
	public int x, y;
	public int w, h;
	private Rect2i parent;

	public Rect2i() {
		this.x = 0;
		this.y = 0;
		this.w = 0;
		this.h = 0;
		this.parent = null;
	}

	public Rect2i(int x, int y, int w, int h, Rect2i parent) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.parent = parent;
	}

	public Rect2i(Rect2i r) {
		this.x = r.x;
		this.y = r.y;
		this.w = r.w;
		this.h = r.h;
		this.parent = r.getParent();
	}

	public void set(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		// parent?
	}

	public Rect2i getParent() {
		return parent;
	}

	public void setParent(Rect2i parent) {
		this.parent = parent;
	}

	public int getLocalCenterX() {
		return x + w / 2;
	}

	public int getLocalCenterY() {
		return y + h / 2;
	}

	public int getLocalRight() {
		return x + w;
	}

	public int getLocalBottom() {
		return y + h;
	}

	public int getGlobalX() {
		if (parent == null)
			return x;
		return x + parent.getGlobalX();
	}

	public int getGlobalY() {
		if (parent == null)
			return y;
		return y + parent.getGlobalY();
	}

	public void move(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public boolean isInside(int globX, int globY) {
		int globalX = this.getGlobalX();
		int globalY = this.getGlobalY();

		if (globX >= globalX && globX <= (globalX + this.w)) {
			if (globY >= globalY && globY <= (globalY + this.h)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMouseInside() {
		return isInside(Mouse.getX(), Display.getHeight() - Mouse.getY());
	}

	public boolean isMidClicked() {
		if (Mouse.isButtonDown(2) && isMouseInside())
			return true;
		return false;
	}

	public boolean isLeftClicked() {
		if (Mouse.isButtonDown(0) && isMouseInside())
			return true;
		return false;
	}

	public boolean isRightClicked() {
		if (Mouse.isButtonDown(1) && isMouseInside())
			return true;
		return false;
	}
}
