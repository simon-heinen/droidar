package worlddata;

import gl.Renderable;

/**
 * This is the basic interface for any object which hat to do with Rendering and
 * which also needs to be updated from time to time. <br>
 * <br>
 * 
 * The existing important subclasses are: <br>
 * 
 * - {@link gl.senegraph.RenderList}: It is a group of {@link worlddata.RenderableEntity}s<br>
 * 
 * - {@link gl.scenegraph.MeshComponent}: A basic {@link gl.scenegraph.Shape} e.g. to draw OpenGL objects or
 * {@link gl.LightSource} to add lighning effects to a scene <br>
 * 
 * @author Spobo
 * 
 */
public interface RenderableEntity extends Entity, Renderable {

}
