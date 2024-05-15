package aperture.simulator;

import aperture.simulator.math.RobotModel;
import com.qualcomm.robotcore.util.ElapsedTime;
import imgui.extension.implot.ImPlot;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

public class Renderer {
    public static long window;
    int width;
    int height;

    Camera camera = new Camera();
    final MouseInput mouseInput;
    final Projection projection;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 200f;

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();


    ElapsedTime timer = new ElapsedTime();

    static ShaderProgram program;
    static ShaderProgram quadProgram;

    static String vertex_shader_src = """
            #version 330

            layout (location=0) in vec3 pos;
            layout (location=1) in vec3 aNormal;

            out vec3 FragPos;
            out vec3 Normal;

            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;

            void main() {
                FragPos =vec3(model * vec4(pos, 1.0));\s
                Normal = aNormal; \s
               \s
                gl_Position = projection * view * vec4(FragPos, 1.0);
            }""";
    //            "    result = color;\n" +
    static String fragment_shader_src = """
            #version 330
            precision mediump float;

            in vec3 Normal; \s
            in vec3 FragPos; \s

            out vec4 outColor;

            uniform vec3 color;
            uniform vec3 viewPos;

            void main() {
                vec3 lightPos = vec3(0,0,0);
                vec3 lightColor = vec3(1,1,1);
                // ambient
                float ambientStrength = 0.5;
                vec3 ambient = ambientStrength * lightColor;
              \t
                // diffuse\s
                vec3 norm = normalize(Normal);
                vec3 lightDir = normalize(lightPos - FragPos);
                float diff = max(dot(norm, lightDir), 0.0);
                vec3 diffuse = diff * lightColor;
            // specular
                float specularStrength = 0.5;
                vec3 viewDir = normalize(viewPos - FragPos);
                vec3 reflectDir = reflect(-lightDir, norm); \s
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
                vec3 specular = specularStrength * spec * lightColor;             \s
                vec3 result = (ambient + diffuse + specular) * color;
                outColor = vec4(result, 1.0);
            }""";
    static String vertex_shader_src_quad = """
            #version 330 core
            layout (location = 0) in vec3 pos;
            layout (location = 1) in vec2 aTexCoord;
            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;

            out vec2 TexCoord;

            void main()
            {
                vec3 aPos =vec3(model * vec4(pos, 1.0));\s
               \s
                gl_Position = projection * view * vec4(aPos, 1.0);
                TexCoord = aTexCoord;
            }""";
    static String fragment_shader_src_quad = """
            #version 330 core
            out vec4 FragColor;
             \s
            in vec2 TexCoord;

            uniform sampler2D ourTexture;

            void main()
            {
                FragColor = texture(ourTexture, TexCoord);
            }""";
    static IntBuffer quad_indicies_buffer;
    int[] quad_indicies = new int[] {
            0,1,2, 0,2,3
    };

    Camera robotCamera = new Camera();
    Projection robotProjection;

    Camera activeCamera;
    Projection activeProjection;

    public Renderer() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                Simulator.stop();
            }
            if (key == GLFW_KEY_B && action == GLFW_RELEASE) {
                robotCameraTrackUserCamera = !robotCameraTrackUserCamera;
            }

            if (key == GLFW_KEY_F && action == GLFW_RELEASE) {
                opencvPreviewFullscreen = !opencvPreviewFullscreen;
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidmode != null;

            width = vidmode.width();
            height = vidmode.height();
            System.out.println("WIDTH, HEIGHT: " + width + " " + height);
            glfwSetWindowSize(window,width,height);
            // Center the window
            glfwSetWindowPos(
                    window,
                    0,
                    0
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();
        ImGui.createContext();
        ImPlot.createContext();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 330");

        glEnable(GL_DEPTH_TEST);

        glDepthFunc(GL_LESS);

        try {
            program = new ShaderProgram();
            program.createVertexShader(vertex_shader_src);
            program.createFragmentShader(fragment_shader_src);
            program.bind();
            program.link();
            quadProgram = new ShaderProgram();
            quadProgram.createVertexShader(vertex_shader_src_quad);
            quadProgram.createFragmentShader(fragment_shader_src_quad);
            quadProgram.bind();
            quadProgram.link();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        mouseInput = new MouseInput(window);
        projection = new Projection(width,height,(float) Math.toRadians(60.0f));
        robotProjection = new Projection(width,height,(float) Math.toRadians(60.0f));
//        GLUtil.setupDebugMessageCallback(System.err);

        Vector3f camPos =calcOnBoardPos(new Vector2f(0f,(25f+5f/8f)/2f+5f),false);
        camPos.add(0f,(float)Math.sin(Math.toRadians(30f))*27f,-(float)Math.cos(Math.toRadians(30f))*27f);
        camera.setPosition(camPos.x,camPos.y,camPos.z);
        camera.setRotation((float)Math.toRadians(30f),(float)Math.toRadians(180f));

        createObjects();

        IntBuffer intBuffer = BufferUtils.createIntBuffer(quad_indicies.length);
        intBuffer.put(quad_indicies);
        intBuffer.flip();

        quad_indicies_buffer = intBuffer;
    }

    LoadedObj hex;

    Texture aprilTagBlueLeft;
    Texture aprilTagBlueCenter;
    Texture aprilTagBlueRight;
    Texture aprilTagRedLeft;
    Texture aprilTagRedCenter;
    Texture aprilTagRedRight;
    Texture groundTexture;

    void createObjects() {

        groundTexture = new Texture("assets/ground.png");

        try {
            //magic number properly scales obj. 1 obj unit = 0.1cm = 0.0393701in
            hex = new LoadedObj("assets/pickle.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        aprilTagBlueLeft = new Texture("assets/blueLeft.PNG");
        aprilTagBlueCenter = new Texture("assets/blueCenter.PNG");
        aprilTagBlueRight = new Texture("assets/blueRight.PNG");
        aprilTagRedLeft = new Texture("assets/redLeft.PNG");
        aprilTagRedCenter = new Texture("assets/redCenter.PNG");
        aprilTagRedRight = new Texture("assets/redRight.PNG");
    }

    double boardZ = 36.0*2.0+(20.0+1.0/8.0)/2.0-(11.0+1.0/4.0);
    double boardHeight = Math.sqrt(Math.pow((25+1/8.0),2) + 35*35);
    void renderToFrameBuffer() {
        program.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(program.programId,"view"), false, activeCamera.getViewMatrix().get(stack.mallocFloat(16)));
            glUniformMatrix4fv(glGetUniformLocation(program.programId,"projection"), false, activeProjection.getProjMatrix().get(stack.mallocFloat(16)));
        }

        renderBox(new Vector3f(1f,1f,1f),new Vector3f(0f,0f,0f),new Vector3f(1f,1f,1f),new Vector3f(0f,0f,0f)); //zero
        renderBox(new Vector3f(0f,0f,0f),new Vector3f(0f,0f,0f),new Vector3f(10,0.5f,0.5f),new Vector3f(1f,0f,0f)); //x axis
        renderBox(new Vector3f(0f,0f,0f),new Vector3f(0f,0f,0f),new Vector3f(0.5f,10,0.5f),new Vector3f(0f,1f,0f)); //y axis
        renderBox(new Vector3f(0f,0f,0f),new Vector3f(0f,0f,0f),new Vector3f(0.5f,0.5f,10),new Vector3f(0f,0f,1f)); //z axis


        renderBox(new Vector3f(24f*1.5f, 35f/2f, (float)boardZ),new Vector3f((float) Math.toRadians(30f), 0f, 0f),new Vector3f(25f+5f/8f, (float) boardHeight,1f).div(2f), new Vector3f(0.2f,0.2f,0.2f)); //board
        renderBox(new Vector3f(-24f*1.5f, 35f/2f, (float)boardZ), new Vector3f((float) Math.toRadians(30f), 0f, 0f),new Vector3f(25f+5f/8f, (float) boardHeight,1f).div(2f), new Vector3f(0.2f,0.2f,0.2f)); //board

        RobotRenderer.render();

        for (int i = 0; i < 11; i++) {
            for (int e = 0; e < 6; e++) {
                hex.render(calcOnBoardPos(calcHexPos(i, e), false), new Vector3f((float) Math.toRadians(120f), (float) Math.toRadians(30f), 0f),new Vector3f(0.0393701f,0.0393701f,0.0393701f),new Vector3f(1.0f,1.0f,1.0f));
            }
        }

        quadProgram.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(quadProgram.programId,"view"), false, activeCamera.getViewMatrix().get(stack.mallocFloat(16)));
            glUniformMatrix4fv(glGetUniformLocation(quadProgram.programId,"projection"), false, activeProjection.getProjMatrix().get(stack.mallocFloat(16)));
        }

        renderTexturedQuad(new Vector3f(0f, 0f, 0f), new Vector3f((float) Math.toRadians(90f), 0f, (float) Math.toRadians(180f)),new Vector3f(72f,72f,1f), groundTexture);

        renderTexturedQuad(calcOnBoardPos(new Vector2f(6f, 5.25f),false),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagBlueLeft);
        renderTexturedQuad(calcOnBoardPos(new Vector2f(0f, 5.25f),false),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagBlueCenter);
        renderTexturedQuad(calcOnBoardPos(new Vector2f(-6f, 5.25f),false),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagBlueRight);
        renderTexturedQuad(calcOnBoardPos(new Vector2f(6f, 5.25f),true),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagRedLeft);
        renderTexturedQuad(calcOnBoardPos(new Vector2f(0f, 5.25f),true),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagRedCenter);
        renderTexturedQuad(calcOnBoardPos(new Vector2f(-6f, 5.25f),true),new Vector3f((float) Math.toRadians(30f), 0f, (float) Math.toRadians(180f)),new Vector3f(2.4f,2.9568f,1f).div(2f),aprilTagRedRight);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    int framebuffer = -1;
    int fbo_color_tex;
    int fbo_rbo;
    public void render() {
        processCamInput();

        if (glfwWindowShouldClose(window) ) { destroy(); }

        glClearColor(0,0.5f,0.5f,1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glViewport(0, 0, width, height);

        activeCamera = camera;
        activeProjection = projection;

        renderToFrameBuffer();

        if(OpenCvCameraFactory.leftCamera.ready) {
            if(framebuffer==-1) {
                setupRenderBuffers();
            }

            int cols = OpenCvCameraFactory.leftCamera.cols;
            int rows = OpenCvCameraFactory.leftCamera.rows;

            glBindFramebuffer(GL_FRAMEBUFFER,framebuffer);
            glClearColor(0,0.0f,0.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(0,0,cols,rows);

            activeCamera = robotCamera;
            activeProjection = robotProjection;

            renderToFrameBuffer();
            passFrameBufferToCamera(true);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0,0,width, height);

            activeCamera = camera;
            activeProjection = projection;

            quadProgram.bind();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                glUniformMatrix4fv(glGetUniformLocation(quadProgram.programId,"view"), false, new Matrix4f().get(stack.mallocFloat(16)));
                glUniformMatrix4fv(glGetUniformLocation(quadProgram.programId,"projection"), false, new Matrix4f().get(stack.mallocFloat(16)));
            }

            glDisable(GL_DEPTH_TEST);
            if(opencvPreviewFullscreen) {
                renderTexturedQuad(new Vector3f(0f,0f,0f),new Vector3f(0f,0f,0f),new Vector3f(1f,1f,1f),new Texture(fbo_color_tex));
            } else {
                renderTexturedQuad(new Vector3f(0.7f,-0.7f,0f),new Vector3f(0f,0f,0f),new Vector3f(0.3f,0.3f,0.3f),new Texture(fbo_color_tex));
            }
            glEnable(GL_DEPTH_TEST);
        }

        doImGui();

        mouseInput.input();

        glfwSwapBuffers(window); // swap the color buffers
        glfwPollEvents();

        timer.reset();
    }

    ByteBuffer interopBuffer = null;
    byte[] interopArray;
    Mat recycledMat;

    void passFrameBufferToCamera(boolean left) {
        if(interopBuffer == null) {
            interopBuffer = BufferUtils.createByteBuffer(OpenCvCameraFactory.leftCamera.cols*OpenCvCameraFactory.leftCamera.rows*3);
            interopArray = new byte[OpenCvCameraFactory.leftCamera.cols*OpenCvCameraFactory.leftCamera.rows*3];
            recycledMat = new Mat(OpenCvCameraFactory.leftCamera.rows,OpenCvCameraFactory.leftCamera.cols, CvType.CV_8UC3);
        }

        glBindTexture(GL_TEXTURE_2D, fbo_color_tex  );

        glGetTexImage(GL_TEXTURE_2D,0,GL_RGB,GL_UNSIGNED_BYTE,interopBuffer);

        interopBuffer.get(interopArray);
        recycledMat.put(0,0,interopArray);

        OpenCvWebcam cam = left ? OpenCvCameraFactory.leftCamera : OpenCvCameraFactory.rightCamera;

        Mat result = cam.sendMatToPipeline(recycledMat);

        result.get(0,0,interopArray);

        interopBuffer.clear();
        interopBuffer.put(interopArray);

        glTexSubImage2D(GL_TEXTURE_2D,0,0,0,OpenCvCameraFactory.leftCamera.cols,OpenCvCameraFactory.leftCamera.rows,GL_RGB,GL_UNSIGNED_BYTE,interopBuffer);


    }

    void setupRenderBuffers() {
        int cols = OpenCvCameraFactory.leftCamera.cols;
        int rows = OpenCvCameraFactory.leftCamera.rows;

        System.out.println("COLS,ROWS:" +cols + " " + rows);
        framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        fbo_color_tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, fbo_color_tex);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, cols, rows, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,fbo_color_tex,0);

        fbo_rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,fbo_rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, cols, rows);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, fbo_rbo);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("RENDER DEBUG: FRAMEBUFFER GOOD!");
        }

//        robotProjection = new Projection(cols,rows,(float) Math.toRadians(45));

        System.out.println("RENDER DEBUG: setup done");
    }

    void doImGui() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("Sim Debug");
        ImGui.text(Simulator.getDebugString());
        ImGui.text(camera.getPosition().toString());
        ImGui.end();

        ImGui.begin("telemetry");
        ImGui.text(telemetryString);
        ImGui.end();


        if(ImPlot.beginPlot("Motor Current")) {
            ImPlot.plotLine("FR", RobotModel.ys,RobotModel.motorCurrentPlot[0], RobotModel.ys.length );
            ImPlot.plotLine("FL", RobotModel.ys,RobotModel.motorCurrentPlot[1], RobotModel.ys.length );
            ImPlot.plotLine("BR", RobotModel.ys,RobotModel.motorCurrentPlot[2], RobotModel.ys.length );
            ImPlot.plotLine("BL", RobotModel.ys,RobotModel.motorCurrentPlot[3], RobotModel.ys.length );
            ImPlot.endPlot();
        }

        if(ImPlot.beginPlot("Motor Speed")) {
            ImPlot.plotLine("FR", RobotModel.ys,RobotModel.motorSpeedPlot[0], RobotModel.ys.length );
            ImPlot.plotLine("FL", RobotModel.ys,RobotModel.motorSpeedPlot[1], RobotModel.ys.length );
            ImPlot.plotLine("BR", RobotModel.ys,RobotModel.motorSpeedPlot[2], RobotModel.ys.length );
            ImPlot.plotLine("BL", RobotModel.ys,RobotModel.motorSpeedPlot[3], RobotModel.ys.length );
            ImPlot.endPlot();
        }

        if(ImPlot.beginPlot("Wheel Slip")) {
            ImPlot.plotLine("FR", RobotModel.ys,RobotModel.wheelSlipPlot[0], RobotModel.ys.length );
            ImPlot.plotLine("FL", RobotModel.ys,RobotModel.wheelSlipPlot[1], RobotModel.ys.length );
            ImPlot.plotLine("BR", RobotModel.ys,RobotModel.wheelSlipPlot[2], RobotModel.ys.length );
            ImPlot.plotLine("BL", RobotModel.ys,RobotModel.wheelSlipPlot[3], RobotModel.ys.length );
            ImPlot.endPlot();
        }

        if(ImPlot.beginPlot("Wheel Force")) {
            ImPlot.plotLine("FR", RobotModel.ys,RobotModel.wheelForcePlot[0], RobotModel.ys.length );
            ImPlot.plotLine("FL", RobotModel.ys,RobotModel.wheelForcePlot[1], RobotModel.ys.length );
            ImPlot.plotLine("BR", RobotModel.ys,RobotModel.wheelForcePlot[2], RobotModel.ys.length );
            ImPlot.plotLine("BL", RobotModel.ys,RobotModel.wheelForcePlot[3], RobotModel.ys.length );
            ImPlot.endPlot();
        }

        if(ImPlot.beginPlot("SPEED")) {
            ImPlot.plotLine("FR", RobotModel.ys,RobotModel.robotSpeedPlot[0], RobotModel.ys.length );
            ImPlot.plotLine("FL", RobotModel.ys,RobotModel.robotSpeedPlot[1], RobotModel.ys.length );
            ImPlot.plotLine("BR", RobotModel.ys,RobotModel.robotSpeedPlot[2], RobotModel.ys.length );
            ImPlot.plotLine("BL", RobotModel.ys,RobotModel.robotSpeedPlot[3], RobotModel.ys.length );
            ImPlot.endPlot();
        }




        ImGui.render();

        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
    Vector3f calcOnBoardPos(Vector2f pos, boolean red) {
        float x = pos.x;
        float y = pos.y;
        float hexOffset = 1f;
        return new Vector3f((red ? 24f*1.5f : -24f*1.5f) + x,y,(y/35f)*20.125f + (float)boardZ - 10.0625f)
                .add(0f,(float)Math.sin(Math.toRadians(120f))*hexOffset,(float)Math.cos(Math.toRadians(120f))*hexOffset);
    }
    Vector2f calcHexPos(int layer, int x) {
        float hexRadius = 1.73205080758f;
        float hexRadiusSmol = 1.5f;
        float hexSide = 0.86602540378f;
        float bottomHexHeight = 7.25f + hexRadius;
        return new Vector2f((layer%2==0 ? -5 : -4)*hexRadiusSmol + hexRadiusSmol*2*x, bottomHexHeight+layer*(hexRadius+hexSide/2f));
    }

    public void setProjMatrix(Matrix4f projMatrix) {
        projection.setProjMatrix(projMatrix);
        robotProjection.setProjMatrix(projMatrix);
    }
    boolean opencvPreviewFullscreen = false;
    boolean robotCameraTrackUserCamera = false;
    void processCamInput() {
        float move = (float)timer.milliseconds() * MOVEMENT_SPEED;
        if (isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
//        if (isKeyPressed(GLFW_KEY_UP)) {
//            camera.moveUp(move);
//        } else if (isKeyPressed(GLFW_KEY_DOWN)) {
//            camera.moveDown(move);
//        }

        if (robotCameraTrackUserCamera) {
            Vector3f pos = camera.getPosition();
            Vector2f rot = camera.getRotation();
            robotCamera.setPosition(pos.x,pos.y,pos.z);
            robotCamera.setRotation(rot.x,rot.y);
            robotProjection = projection;
        } else {
            robotCamera.setPosition((float)-(Simulator.positions.x+Math.cos(Simulator.positions.h)*RobotRenderer.drivePodLength/2.0),0,
                    (float)(Simulator.positions.y+Math.sin(Simulator.positions.h)*RobotRenderer.drivePodLength/2.0));
            robotCamera.setRotation(0,(float)Simulator.positions.h);
        }



        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
    }

    void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.exit(0);
    }
    static String telemetryString = new String();
    public static void updateTelemetryString(String str) {
        telemetryString = str;
    }

    static Integer boxVao = null;
    static Integer boxVbo = null;
    public static void renderBox(Vector3f pos, Vector3f rot, Vector3f scale,  Vector3f color) {
        if(boxVbo==null) {
            Vector3f[] vertices =
                    new Vector3f[] {
                            new Vector3f(-1, -1, -1),
                            new Vector3f(1, -1, -1),
                            new Vector3f(1, 1, -1),
                            new Vector3f(-1, 1, -1),
                            new Vector3f(-1, -1, 1),
                            new Vector3f(1, -1, 1),
                            new Vector3f(1, 1, 1),
                            new Vector3f(-1, 1, 1)
                    };

            Vector3f[] normals =
                    new Vector3f[] {
                            new Vector3f(0, 0, 1),
                            new Vector3f(1, 0, 0),
                            new Vector3f(0, 0, -1),
                            new Vector3f(-1, 0, 0),
                            new Vector3f(0, 1, 0),
                            new Vector3f(0, -1, 0)
                    };

            int[] indices =
                    new int[]{
                            0, 1, 3, 3, 1, 2,
                            1, 5, 2, 2, 5, 6,
                            5, 4, 6, 6, 4, 7,
                            4, 0, 7, 7, 0, 3,
                            3, 2, 7, 7, 2, 6,
                            4, 5, 0, 0, 5, 1
                    };

            float[] data = new float[6*36];

            for(int i=0; i<36; i++) {
                data[i*6] = vertices[indices[i]].x;
                data[i*6 + 1] = vertices[indices[i]].y;
                data[i*6 + 2] = vertices[indices[i]].z;
                data[i*6 + 3] = normals[indices[i / 6]].x;
                data[i*6 + 4] = normals[indices[i / 6]].y;
                data[i*6 + 5] = normals[indices[i / 6]].z;
            }

            boxVao = glGenVertexArrays();
            glBindVertexArray(boxVao);

            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
            floatBuffer.put(data);
            floatBuffer.flip();

            boxVbo = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, boxVbo);
            glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,3,GL_FLOAT,false,6 * 4,0);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,3,GL_FLOAT,false,6 * 4,3 * 4);
        }
        glBindVertexArray(boxVao);
        glBindBuffer(GL_ARRAY_BUFFER, boxVbo);

        rot.x = (float)Math.toDegrees(rot.x);
        rot.y = (float)Math.toDegrees(rot.y);
        rot.z = (float)Math.toDegrees(rot.z);

        Matrix4f world = getModelMatrix(pos, rot,scale);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(program.programId,"model"), false, world.get(stack.mallocFloat(16)));
            glUniform3f(glGetUniformLocation(program.programId,"color"),color.x,color.y,color.z);
        }

        glDrawArrays(GL_TRIANGLES,0, 6 * 6);
    }

    static Integer quadVao = null;
    static Integer quadVbo = null;
    public static void renderTexturedQuad(Vector3f pos, Vector3f rot, Vector3f scale, Texture texture) {
        if(quadVao == null) {
            float[] data = new float[] {
                    1f,1f,0f, 1f,1f,
                    -1f,1f,0f, 0f,1f,
                    -1f,-1f,0f, 0f,0f,
                    1f,-1f,0f, 1f,0f
            };
            quadVao = glGenVertexArrays();
            glBindVertexArray(quadVao);

            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
            floatBuffer.put(data);
            floatBuffer.flip();

            quadVbo = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, quadVbo);
            glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,3,GL_FLOAT,false,5 * 4,0);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,2,GL_FLOAT,false,5 * 4,3 * 4);
        }
        glUniform1i(glGetUniformLocation(quadProgram.programId,"ourTexture"),0);
        glActiveTexture(GL_TEXTURE0);

        texture.bind();

        glBindVertexArray(quadVao);
        glBindBuffer(GL_ARRAY_BUFFER, quadVbo);

        rot.x = (float)Math.toDegrees(rot.x);
        rot.y = (float)Math.toDegrees(rot.y);
        rot.z = (float)Math.toDegrees(rot.z);

        Matrix4f world = getModelMatrix(pos, rot,scale);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(quadProgram.programId,"model"), false, world.get(stack.mallocFloat(16)));
        }

        glDrawElements(GL_TRIANGLES,quad_indicies_buffer);
        texture.unbind();
    }
    static Matrix4f getModelMatrix(Vector3f offset, Vector3f rotation, Vector3f scale) {
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
        return modelMatrix;
    }

    static class LoadedObj {
        static Integer vao = null;
        static Integer vbo = null;
        final int size;

        LoadedObj(String path) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(path));

            List<Vector3f> verticies = new ArrayList<>();
            List<float[]> vertexData = new ArrayList<>();

            for(String line: lines) {
                String[] tokens = line.split("\\s+");

                switch (tokens[0]) {
                    case "v" ->
                            verticies.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                    case "f" -> {
                        Vector3f v0 = verticies.get(Integer.parseInt(tokens[1]) - 1);
                        Vector3f v1 = verticies.get(Integer.parseInt(tokens[2]) - 1);
                        Vector3f v2 = verticies.get(Integer.parseInt(tokens[3]) - 1);
                        Vector3f u = new Vector3f(v1.x, v1.y, v1.z);
                        Vector3f v = new Vector3f(v2.x, v2.y, v2.z);
                        u.sub(v0);
                        v.sub(v0);
                        float nx = u.y * v.z - u.z * v.y;
                        float ny = u.z * v.x - u.x * v.z;
                        float nz = u.x * v.y - u.y * v.x;
                        float[] faceData = new float[]{
                                v0.x, v0.y, v0.z, nx, ny, nz,
                                v1.x, v1.y, v1.z, nx, ny, nz,
                                v2.x, v2.y, v2.z, nx, ny, nz,
                        };
                        vertexData.add(faceData);
                    }
                    default -> {
                    }
                }
            }

            FloatBuffer data = BufferUtils.createFloatBuffer(vertexData.size() * 3 * 6);
            for(float[] x : vertexData) {
                data.put(x);
            }
            data.flip();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            vbo = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,3,GL_FLOAT,false,6 * 4,0);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,3,GL_FLOAT,false,6 * 4,3 * 4);

            this.size = vertexData.size() * 3;
        }

        void render(Vector3f pos, Vector3f rot, Vector3f scale, Vector3f color) {
//            program.bind();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            rot.x = (float)Math.toDegrees(rot.x);
            rot.y = (float)Math.toDegrees(rot.y);
            rot.z = (float)Math.toDegrees(rot.z);

            Matrix4f world = getModelMatrix(pos, rot,scale);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                glUniformMatrix4fv(glGetUniformLocation(program.programId,"model"), false, world.get(stack.mallocFloat(16)));
                glUniform3f(glGetUniformLocation(program.programId,"color"),color.x,color.y,color.z);
            }

            glDrawArrays(GL_TRIANGLES,0, size);
        }
    }
}