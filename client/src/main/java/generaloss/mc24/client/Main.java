package generaloss.mc24.client;

import generaloss.mc24.client.block.BlockFace;
import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.block.BlockVertex;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.client.resource.ResourcesRegistry;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.session.ClientSession;
import generaloss.mc24.client.session.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.registry.Registries;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.AlDevices;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;
import jpize.util.font.Font;
import jpize.util.font.FontLoader;
import jpize.util.math.Maths;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main extends JpizeApplication {

    private final ResourcesRegistry resources;
    private final ClientRegistries registries;
    private final Server localServer;
    private final Font font;
    private final ScreenDispatcher screens;
    private final ClientSession session;

    public Main() {
        this.resources = new ResourcesRegistry();
        this.registries = new ClientRegistries();
        this.localServer = new Server(registries);
        this.font = FontLoader.loadDefault();
        this.screens = new ScreenDispatcher();
        this.session = new ClientSession(this);
    }

    public ResourcesRegistry resources() {
        return resources;
    }

    public Server localServer() {
        return localServer;
    }

    public ScreenDispatcher screens() {
        return screens;
    }


    @Override
    public void init() {
        System.out.println("Initializing client");
        // audio
        AlDevices.openDevice();
        // screens
        screens.register(new TitleScreen(this));
        screens.register(new SessionScreen(this, session));
        // resources
        this.loadResources();
        // set menu screen
        screens.show("title");
        startLocalSession();
    }


    private void loadResources() {
        // load block models
        final String blockModelsPath = "/resources/models/blocks/";
        final ExternalResource defaultBlockModelsRes = Resource.external(resources.getDefaultDirectory() + blockModelsPath);
        System.out.println("Loading " + defaultBlockModelsRes.list().length + " block models..");
        for(ExternalResource blockModelRes: defaultBlockModelsRes.listRes()){
            loadBlockModel(Resource.external(blockModelsPath + blockModelRes.name()));
        }
        // apply default asset pack
        resources.reloadAll();
    }

    private void loadBlockModel(Resource resource) {
        BlockModel model = new BlockModel();

        final JSONObject jsonObject = new JSONObject(resource.readString());

        final String id = jsonObject.getString("ID");

        model.clear();
        model.setDontHideSameBlockFaces(jsonObject.getBoolean("dont_hide_same_block_faces"));

        final JSONObject jsonFaces = jsonObject.getJSONObject("faces");
        for(String faceKey: jsonFaces.keySet()){
            final JSONObject jsonFace = jsonFaces.getJSONObject(faceKey);
            // texture
            final String textureID = jsonFace.getString("texture_ID");
            // vertices
            final JSONArray vertices = jsonFace.getJSONArray("vertices");
            final BlockVertex[] verticesArray = new BlockVertex[vertices.length()];
            for(int i = 0; i < vertices.length(); i++){
                final JSONArray jsonVertex = vertices.getJSONArray(i);
                final float[] vertexArray = new float[BlockVertex.SIZE];
                for(int j = 0; j < jsonVertex.length(); j++)
                    vertexArray[j] = jsonVertex.getFloat(j);

                verticesArray[i] = new BlockVertex(
                        vertexArray[0], vertexArray[1], vertexArray[2], // position
                        vertexArray[3], vertexArray[4], // texcoord
                        vertexArray[5], vertexArray[6], vertexArray[7], vertexArray[8] // color
                );
            }

            model.addFace(new BlockFace(textureID, verticesArray));
        }

        final Block block = registries.BLOCK.get(id);
        if(block == null)
            throw new IllegalStateException("Block model cannot be loaded. Block with ID '" + id + "' is not exists.");

        registries.BLOCK_MODEL.register(blockState, model);
    }


    public void startLocalSession() {
        System.out.println("Starting local session..");
        localServer.run(Maths.random(64000, 64999));
        session.connect("localhost", localServer.getPort());
        screens.show("session");
    }


    @Override
    public void update() {
        // --- reload resources test --- //
        if(Key.NUM_0.up()){
            resources.resetDirectory();
            resources.reloadAll();
        }
        if(Key.NUM_1.up()){
            resources.setDirectory("resource-packs/test-pack-1");
            resources.reloadAll();
        }
        if(Key.NUM_2.up()){
            resources.setDirectory("resource-packs/test-pack-2");
            resources.reloadAll();
        }

        // fullscreen
        if(Key.F11.down())
            Jpize.window().toggleFullscreen();

        // secreens
        screens.update();
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
        screens.render();
        font.drawText("FPS: " + Jpize.getFPS(), 10, Jpize.getHeight() - 10 - font.getLineAdvanceScaled());
    }

    @Override
    public void resize(int width, int height) {
        screens.resize(width, height);
    }

    @Override
    public void dispose() {
        screens.dispose();
        resources.dispose();
        font.dispose();
        AlDevices.dispose();
    }


    public static void main(String[] args) {
        // wayland=>x11 fix
        if(System.getProperty("os.name").equals("Linux"))
            Glfw.glfwInitHintPlatform(GlfwPlatform.X11);

        // parse arguments
        final ArgsMap argsMap = new ArgsMap(args);
        final int width = argsMap.getInt("width", 1280);
        final int height = argsMap.getInt("height", 720);

        // create window
        Jpize.create("Minecraft-24 Client", width, height)
            .icon("/icon.png")
            .build()
            .setApp(INSTANCE = new Main());

        // run application
        Jpize.run();
    }

    private static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }

}