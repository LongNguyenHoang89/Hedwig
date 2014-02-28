using OpenTK;
using OpenTK.Graphics.OpenGL;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestClient
{
    class GLRenderWindow : GLControl
    {
        public bool isLoaded { get; set; }

        public GLRenderWindow()
            : base()
        {
            this.Dock = System.Windows.Forms.DockStyle.Fill;
            this.Paint += GLRenderWindow_Paint;
            this.Load += GLRenderWindow_Load;
            isLoaded = false;
        }

        protected override void OnResize(EventArgs e)
        {
            if (isLoaded)
            {
                base.OnResize(e);

                GL.Viewport(ClientRectangle.X, ClientRectangle.Y, ClientRectangle.Width, ClientRectangle.Height);

                Matrix4 projection = Matrix4.CreatePerspectiveFieldOfView((float)Math.PI / 4, Width / (float)Height, 1.0f, 64.0f);
                GL.MatrixMode(MatrixMode.Projection);
                GL.LoadMatrix(ref projection);
            }
        }

        void GLRenderWindow_Load(object sender, EventArgs e)
        {
            isLoaded = true;
            GL.ClearColor(Color.SkyBlue);
            GL.Enable(EnableCap.DepthTest);
        }

        void GLRenderWindow_Paint(object sender, System.Windows.Forms.PaintEventArgs e)
        {
            GL.Clear(ClearBufferMask.ColorBufferBit | ClearBufferMask.DepthBufferBit);
            Matrix4 modelview = Matrix4.LookAt(Vector3.Zero, Vector3.UnitZ, Vector3.UnitY);
            GL.MatrixMode(MatrixMode.Modelview);
            GL.LoadMatrix(ref modelview);

            GL.Begin(BeginMode.Triangles);

            GL.Color3(1.0f, 1.0f, 0.0f); GL.Vertex3(-1.0f, -1.0f, 4.0f);
            GL.Color3(1.0f, 0.0f, 0.0f); GL.Vertex3(1.0f, -1.0f, 4.0f);
            GL.Color3(0.2f, 0.9f, 1.0f); GL.Vertex3(0.0f, 1.0f, 4.0f);

            GL.End();

            this.SwapBuffers();
        }
    }
}
