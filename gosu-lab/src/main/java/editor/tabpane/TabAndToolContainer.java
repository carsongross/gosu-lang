package editor.tabpane;

import editor.splitpane.EmptyCaptionBar;
import editor.splitpane.ICaptionActionListener;
import editor.splitpane.ICaptionBar;
import editor.util.EditorUtilities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class TabAndToolContainer extends JPanel implements ICaptionBar
{
  private TabPane _tabPane;
  private TabContainer _tabContainer;
  private ToolContainer _toolContainer;
  private ICaptionActionListener.ActionType _captionType;
  private TabPosition _activePosition;
  private TabAndToolContainer _copiedFrom;
  private List<ICaptionActionListener> _captionActionListeners;
  private List<ChangeListener> _captionTypeListeners;

  public TabAndToolContainer( TabPane tabPane )
  {
    super( new BorderLayout() );
    _tabPane = tabPane;
    _tabContainer = _tabPane.getTabContainer();
    _toolContainer = _tabPane.getToolContainer();
    _activePosition = _tabContainer.getTabPosition();
    _captionActionListeners = new ArrayList<>();
    _captionTypeListeners = new ArrayList<>();
    configUi();
  }

  private TabAndToolContainer( TabAndToolContainer source )
  {
    this( source._tabPane );
    _copiedFrom = source;
  }

  public Component[] getDecorations()
  {
    return _toolContainer.getToolBar().getComponents();
  }

  public void addDecoration( JComponent decoration, int iIndex )
  {
    _toolContainer.getToolBar().add( decoration, iIndex );
  }

  public void setCaptionType( ICaptionActionListener.ActionType actionType )
  {
    _captionType = actionType;
    if( _captionType != ICaptionActionListener.ActionType.MINIMIZE )
    {
      if( _copiedFrom != null )
      {
        // This is a minimized copy. Notify the original container.
        _copiedFrom.setCaptionType( actionType );
      }
      else
      {
        _tabContainer.setTabPosition( _activePosition );
        configUi();
      }
    }
    fireCaptionTypePerformed();
  }
  public ICaptionActionListener.ActionType getCaptionType()
  {
    return _captionType;
  }

  public ICaptionBar getMinimizedPanel( TabPosition tabPosition )
  {
    _tabContainer.setTabPosition( tabPosition );
    _tabContainer.addSelectionListener(
      new ChangeListener()
      {
        public void stateChanged( ChangeEvent e )
        {
          fireCaptionActionPerformed( ICaptionActionListener.ActionType.RESTORE  );
          _tabContainer.removeSelectionListener( this );
        }
      } );
    if( _tabContainer.getTabCount() > 0 )
    {
      TabAndToolContainer copy = new TabAndToolContainer( this );
      copy.setBorder( BorderFactory.createMatteBorder( 1, 1, 1, 1, EditorUtilities.CONTROL_SHADOW ) );
      return copy;
    }
    else
    {
      return new EmptyCaptionBar();
    }
  }

  public void addCaptionActionListener( ICaptionActionListener captionListener )
  {
    if( !_captionActionListeners.contains( captionListener ) )
    {
      _captionActionListeners.add( captionListener );
    }
  }
  public void removeCaptionActionListener( ICaptionActionListener captionListener )
  {
    _captionActionListeners.remove( captionListener );
  }

  void fireCaptionActionPerformed( ICaptionActionListener.ActionType actionType )
  {
    ICaptionActionListener[] listeners = _captionActionListeners.toArray( new ICaptionActionListener[_captionActionListeners.size()] );
    if( listeners.length == 0 )
    {
      return;
    }
    for( int i = listeners.length-1; i >= 0; i-- )
    {
      listeners[i].captionActionPerformed( _tabPane, actionType );
    }
  }

  public void addCaptionTypeListener( ChangeListener captionListener )
  {
    if( !_captionTypeListeners.contains( captionListener ) )
    {
      _captionTypeListeners.add( captionListener );
    }
  }
  @SuppressWarnings("UnusedDeclaration")
  public void removeCaptionTypeListener( ChangeListener captionListener )
  {
    _captionTypeListeners.remove( captionListener );
  }

  private void fireCaptionTypePerformed()
  {
    ChangeListener[] listeners = _captionTypeListeners.toArray( new ChangeListener[_captionTypeListeners.size()] );
    if( listeners.length == 0 )
    {
      return;
    }
    ChangeEvent e = new ChangeEvent( this );
    for( int i = listeners.length-1; i >= 0; i-- )
    {
      listeners[i].stateChanged( e );
    }
  }

  private void configUi()
  {
    setOpaque( false );
    add( _tabContainer, BorderLayout.CENTER );

    TabPosition tabPosition = _tabContainer.getTabPosition();
    if( tabPosition == TabPosition.TOP )
    {
      add( _toolContainer, BorderLayout.EAST );
    }
    else if( tabPosition == TabPosition.BOTTOM )
    {
      add( _toolContainer, BorderLayout.EAST );
    }
    else if( tabPosition == TabPosition.RIGHT )
    {
      add( _toolContainer, BorderLayout.SOUTH );
    }
    else if( tabPosition == TabPosition.LEFT )
    {
      add( _toolContainer, BorderLayout.SOUTH );
    }
    else
    {
      throw new IllegalStateException( "Unknown TabPosition " );
    }
  }
}
