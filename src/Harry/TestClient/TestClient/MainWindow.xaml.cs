
using OpenTK;
using OpenTK.Graphics.OpenGL4;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Windows;

namespace TestClient
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly int PORT = 6666;
        private SocketServer _server;
        private GLControl _glControl;

        public MainWindow()
        {
            InitializeComponent();

            _server = new SocketServer(PORT);

            this.HelloButton.Click += HelloButton_Click;
            initGL();
        }

        private void initGL()
        {
            _glControl = new GLRenderWindow();
            this.WindowHost.Child = _glControl;
        }

        void HelloButton_Click(object sender, RoutedEventArgs e)
        {
            _server.sendCommand("Fly");
        }
    }
}
