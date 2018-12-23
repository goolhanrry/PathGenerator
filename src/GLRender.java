import java.awt.event.*;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

class GLRender implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private int canvasWidth, canvasHeight;
    private float mouseX, mouseY, offsetX = 0, offsetY = 0, newOffsetX = 0, newOffsetY = 0, scale = 0.9f;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // 设置背景颜色
        gl.glClearColor(0.13f, 0.15f, 0.18f, 0);

        // 设置绘图模式
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        // 设置画笔样式
        gl.glLineWidth(2f);
        gl.glColor3f(1, 0.8f, 0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(x, y, width, height);

        // 保存绘图组件尺寸
        canvasWidth = width;
        canvasHeight = height;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // 清空画布
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // 刷新相机视角
        gl.glTranslatef(offsetX + newOffsetX, offsetY + newOffsetY, 0);
        gl.glLoadIdentity();

        // 若无地图对象则返回
        if (PathGenerator.map == null) {
            return;
        }

        // 绘制图像
        if (drawable.getSurfaceWidth() * PathGenerator.map.dY >= drawable.getSurfaceHeight() * PathGenerator.map.dX) {
            for (GeoPolyline item : PathGenerator.map.polyline) {
                gl.glBegin(GL.GL_LINES);
                for (int i = 0; i < item.size; i++) {
                    gl.glVertex2f(2 * scale * (item.pts[i].x - PathGenerator.map.mX) * canvasHeight / (PathGenerator.map.dY * canvasWidth), 2 * scale * (item.pts[i].y - PathGenerator.map.mY) / PathGenerator.map.dY);
                }
                gl.glEnd();
            }
        } else {
            for (GeoPolyline item : PathGenerator.map.polyline) {
                gl.glBegin(GL.GL_LINES);
                for (int i = 0; i < item.size; i++) {
                    gl.glVertex2f(2 * scale * (item.pts[i].x - PathGenerator.map.mX) / PathGenerator.map.dX, 2 * scale * (item.pts[i].y - PathGenerator.map.mY) * canvasWidth / (PathGenerator.map.dX * canvasHeight));
                }
                gl.glEnd();
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // method body
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 判断是否按下左键
        if (e.getButton() == MouseEvent.BUTTON1) {
            // 初始化鼠标位置
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // 固化图像偏移量
        offsetX += newOffsetX;
        offsetY += newOffsetY;

        // 恢复图像偏移增量
        newOffsetX = 0;
        newOffsetY = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // 判断是否按下左键
        if (e.getButton() == MouseEvent.BUTTON1) {
            // 计算图像偏移量
            newOffsetX = 4 * (e.getX() - mouseX) / canvasWidth;
            newOffsetY = 4 * (mouseY - e.getY()) / canvasHeight;

            // 重绘图像
            MainWindow.mapCanvas.display();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // 更新绘制比例
        scale -= 0.02 * e.getPreciseWheelRotation();

        // 限制缩小倍数
        if (scale < 0.9f) {
            scale = 0.9f;
        }

        // 重绘图像
        MainWindow.mapCanvas.display();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
}
