


package jme3test.light.testPerso;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.jme3.util.TangentBinormalGenerator;

public class TestDirectionalLightShadow extends SimpleApplication{

    public static final int SHADOWMAP_SIZE = 1024;
    private DirectionalLightShadowRenderer dlsr;
    private DirectionalLightShadowFilter dlsf;
    private Spatial[] obj;
    private Material[] mat;
   
    private Geometry ground;
    private Material matGroundL;
    private AmbientLight al;

    public static void main(String[] args) {
        TestDirectionalLightShadow app = new TestDirectionalLightShadow();
        app.start();
    }
    
    //Charge la scene
    public void loadScene() {
        
        obj = new Spatial[2];
        mat = new Material[2];
        
        //obj0 set up
        mat[0] = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        obj[0] = new Geometry("sphere", new Sphere(30, 30, 2));
        obj[0].setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(obj[0]);

        
        //obj1 type set up
        mat[1] = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");
        mat[1].setBoolean("UseMaterialColors", true);
        mat[1].setColor("Ambient", ColorRGBA.White);
        mat[1].setColor("Diffuse", ColorRGBA.White.clone());
        obj[1] = new Geometry("cube", new Box(1.0f, 1.0f, 1.0f));
        obj[1].setShadowMode(ShadowMode.CastAndReceive);
        TangentBinormalGenerator.generate(obj[1]);

        
        
        //Basic objects
        
        //Met une sphere de type obj0 dans le monde
        Spatial t = obj[0];
        t.setLocalScale(10f);
        t.setMaterial(mat[1]);
        rootNode.attachChild(t);
        t.setLocalTranslation(0, 25, 0);
                
        Spatial t1 = obj[1].clone(false);
        t1.setLocalScale(10f);
        t1.setMaterial(mat[1]);
        rootNode.attachChild(t1);
        t1.setLocalTranslation(40, 70, 30);
        

        

        //ground
        Box b = new Box(1000, 2, 1000);
        b.scaleTextureCoordinates(new Vector2f(10, 10));
        ground = new Geometry("soil", b);
        ground.setLocalTranslation(0, 10, 550);
        matGroundL = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matGroundL.setTexture("DiffuseMap", grass);
        ground.setMaterial(matGroundL);
        ground.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(ground);

        //cube perso
        //Spatial cube1 = assetManager.loadModel("Models/cube1/cube1.mesh.j3o");
        Spatial cube1 = assetManager.loadModel("Models/gege2/gege.mesh.j3o");
        
        cube1.setShadowMode(ShadowMode.CastAndReceive);
        cube1.scale(15);
        cube1.move(30, 50, -60);
        
        rootNode.attachChild(cube1);
        
        
        //set up directionalLight
        l = new DirectionalLight();
        l.setDirection(new Vector3f(-1, -1, -0.8f));
        rootNode.addLight(l);

        //set up sky
        Spatial sky = SkyFactory.createSky(assetManager,
                "Scenes/Beach/FullskiesSunset0068.dds", EnvMapType.CubeMap);
        sky.setLocalScale(350);

        rootNode.attachChild(sky);
    }
            DirectionalLight l;


    @Override
    public void simpleInitApp() {

        
        //parametre la camera
        cam.setLocation(new Vector3f(3.3720117f, 42.838284f, -83.43792f));
        cam.setRotation(new Quaternion(0.13833192f, -0.08969371f, 0.012581267f, 0.9862358f));
        flyCam.setMoveSpeed(100);

        loadScene();//charge la scene

        //PARAMETRAGE DU POST PROCESSING
        //shadow renderer
        /*
                
        dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 1);
        dlsr.setLight(l);
        //dlsr.setLambda(0.55f);
        //dlsr.setShadowIntensity(0.8f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsr.displayDebug();
        viewPort.addProcessor(dlsr);
        */

        //shadow filter
        dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 2);
     dlsf.setLight(l);
    dlsf.setEnabledStabilization(true);
    dlsf.setShadowIntensity(0.4f);
    dlsf.setEdgesThickness(10);
    dlsf.setShadowCompareMode(CompareMode.Hardware);
    dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
            FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

    fpp.addFilter(dlsf);

        viewPort.addProcessor(fpp);

    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }

   
}