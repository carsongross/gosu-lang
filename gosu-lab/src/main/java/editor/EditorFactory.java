package editor;

import editor.undo.AtomicUndoManager;
import gw.lang.parser.IScriptPartId;
import gw.lang.parser.ScriptabilityModifiers;
import gw.lang.reflect.IType;
import gw.lang.reflect.gs.IGosuClass;
import java.io.File;

/**
 */
public class EditorFactory
{
  public static EditorHost createEditor( File file, IScriptPartId partId )
  {
    FileTree fileTree = FileTreeUtil.find( file );
    if( fileTree == null )
    {
      //## todo: add this to the root as an external filetree?
      fileTree = FileTreeUtil.makeExternalFileTree( file, partId == null ? null : partId.getContainingTypeName() );
    }

    IType type = fileTree.getType();
    return createEditor( file, type );
  }

  private static EditorHost createEditor( File file, IType type )
  {
    if( type instanceof IGosuClass )
    {
      GosuEditor editor = new GosuEditor( new GosuClassLineInfoManager(),
                            new AtomicUndoManager( 10000 ),
                            ScriptabilityModifiers.SCRIPTABLE,
                            new DefaultContextMenuHandler(),
                            false, true );
      initEditorMode( file, editor );
      return editor;
    }
    else
    {
      return new StandardEditor( new SimpleLineInfoManager(), type );
      //return new PlainTextEditor();
    }

  }

  private static GosuEditor initEditorMode( File file, GosuEditor editor )
  {
    if( file != null && file.getName() != null )
    {
      if( file.getName().endsWith( ".gsx" ) )
      {
        editor.setProgram( false );
        editor.setTemplate( false );
        editor.setClass( false );
        editor.setEnhancement( true );
      }
      else if( file.getName().endsWith( ".gs" ) )
      {
        editor.setProgram( false );
        editor.setTemplate( false );
        editor.setClass( true );
        editor.setEnhancement( false );
      }
      else if( file.getName().endsWith( ".gst" ) )
      {
        editor.setProgram( false );
        editor.setTemplate( true );
        editor.setClass( false );
        editor.setEnhancement( false );
      }
      else
      {
        editor.setProgram( true );
        editor.setTemplate( false );
        editor.setClass( false );
        editor.setEnhancement( false );
      }
    }
    return editor;
  }

}
