  


<!DOCTYPE html>
<html>
  <head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# githubog: http://ogp.me/ns/fb/githubog#">
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Chess-Service/CentralServer/src/core/Bot.java at master · ChessEP/Chess-Service</title>
    <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="GitHub" />
    <link rel="fluid-icon" href="https://github.com/fluidicon.png" title="GitHub" />
    <link rel="apple-touch-icon" sizes="57x57" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="114x114" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="72x72" href="/apple-touch-icon-144.png" />
    <link rel="apple-touch-icon" sizes="144x144" href="/apple-touch-icon-144.png" />
    <link rel="logo" type="image/svg" href="http://github-media-downloads.s3.amazonaws.com/github-logo.svg" />
    <link rel="xhr-socket" href="/_sockets" />


    <meta name="msapplication-TileImage" content="/windows-tile.png" />
    <meta name="msapplication-TileColor" content="#ffffff" />
    <meta name="selected-link" value="repo_source" data-pjax-transient />

    
    
    <link rel="icon" type="image/x-icon" href="/favicon.ico" />

    <meta content="authenticity_token" name="csrf-param" />
<meta content="C23ZPCr39zqKQg8PjNG6sCBOeCpyhmhT+gLI1/UD2+A=" name="csrf-token" />

    <link href="https://a248.e.akamai.net/assets.github.com/assets/github-d5919c2c85462b6c3d5b24d94361e0cfe7c6d3c1.css" media="all" rel="stylesheet" type="text/css" />
    <link href="https://a248.e.akamai.net/assets.github.com/assets/github2-c199e1478f7546c043f0929b27d7f1688483a16d.css" media="all" rel="stylesheet" type="text/css" />
    


      <script src="https://a248.e.akamai.net/assets.github.com/assets/frameworks-92d138f450f2960501e28397a2f63b0f100590f0.js" type="text/javascript"></script>
      <script src="https://a248.e.akamai.net/assets.github.com/assets/github-c0439b281928dd009b3a75e187d4b94c7abe5890.js" type="text/javascript"></script>
      
      <meta http-equiv="x-pjax-version" content="14f79f6751c8415cf388e9730a05fea0">

        <link data-pjax-transient rel='permalink' href='/ChessEP/Chess-Service/blob/0452fb880ee9787ca670d2d2a137a8881f3d5673/CentralServer/src/core/Bot.java'>
    <meta property="og:title" content="Chess-Service"/>
    <meta property="og:type" content="githubog:gitrepository"/>
    <meta property="og:url" content="https://github.com/ChessEP/Chess-Service"/>
    <meta property="og:image" content="https://secure.gravatar.com/avatar/bc8ba51c95db5f78d989863a0975a1ef?s=420&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"/>
    <meta property="og:site_name" content="GitHub"/>
    <meta property="og:description" content="Contribute to Chess-Service development by creating an account on GitHub."/>
    <meta property="twitter:card" content="summary"/>
    <meta property="twitter:site" content="@GitHub">
    <meta property="twitter:title" content="ChessEP/Chess-Service"/>

    <meta name="description" content="Contribute to Chess-Service development by creating an account on GitHub." />

  <link href="https://github.com/ChessEP/Chess-Service/commits/master.atom" rel="alternate" title="Recent Commits to Chess-Service:master" type="application/atom+xml" />

  </head>


  <body class="logged_in page-blob windows vis-public env-production  ">
    <div id="wrapper">

      

      
      
      

      <div class="header header-logged-in true">
  <div class="container clearfix">

    <a class="header-logo-invertocat" href="https://github.com/">
  <span class="mega-icon mega-icon-invertocat"></span>
</a>

    <div class="divider-vertical"></div>

      <a href="/ChessEP/Chess-Service/notifications" class="notification-indicator tooltipped downwards contextually-unread" title="You have unread notifications in this repository">
    <span class="mail-status unread"></span>
  </a>
  <div class="divider-vertical"></div>


      <div class="command-bar js-command-bar  in-repository">
          <form accept-charset="UTF-8" action="/search" class="command-bar-form" id="top_search_form" method="get">
  <a href="/search/advanced" class="advanced-search-icon tooltipped downwards command-bar-search" id="advanced_search" title="Advanced search"><span class="mini-icon mini-icon-advanced-search "></span></a>

  <input type="text" data-hotkey="/ s" name="q" id="js-command-bar-field" placeholder="Search or type a command" tabindex="1" data-username="clemgaut" autocapitalize="off">

    <input type="hidden" name="nwo" value="ChessEP/Chess-Service" />

    <div class="select-menu js-menu-container js-select-menu search-context-select-menu">
      <span class="minibutton select-menu-button js-menu-target">
        <span class="js-select-button">This repository</span>
      </span>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container">
        <div class="select-menu-modal">

          <div class="select-menu-item js-navigation-item selected">
            <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
            <input type="radio" name="search_target" value="repository" checked="checked" />
            <div class="select-menu-item-text js-select-button-text">This repository</div>
          </div> <!-- /.select-menu-item -->

          <div class="select-menu-item js-navigation-item">
            <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
            <input type="radio" name="search_target" value="global" />
            <div class="select-menu-item-text js-select-button-text">All repositories</div>
          </div> <!-- /.select-menu-item -->

        </div>
      </div>
    </div>

  <span class="mini-icon help tooltipped downwards" title="Show command bar help">
    <span class="mini-icon mini-icon-help"></span>
  </span>

    <input type="hidden" name="type" value="Code" />

  <input type="hidden" name="ref" value="cmdform">

  <div class="divider-vertical"></div>

    <input type="hidden" class="js-repository-name-with-owner" value="ChessEP/Chess-Service"/>
    <input type="hidden" class="js-repository-branch" value="master"/>
    <input type="hidden" class="js-repository-tree-sha" value="31037eadfe3c054735ffe49edee9c3dc78639c0c"/>
</form>
        <ul class="top-nav">
            <li class="explore"><a href="https://github.com/explore">Explore</a></li>
            <li><a href="https://gist.github.com">Gist</a></li>
            <li><a href="/blog">Blog</a></li>
          <li><a href="http://help.github.com">Help</a></li>
        </ul>
      </div>

    

  

    <ul id="user-links">
      <li>
        <a href="https://github.com/clemgaut" class="name">
          <img height="20" src="https://secure.gravatar.com/avatar/0101117310b0e43fe2bab7b684d6344e?s=140&amp;d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png" width="20" /> clemgaut
        </a>
      </li>

        <li>
          <a href="/new" id="new_repo" class="tooltipped downwards" title="Create a new repo">
            <span class="mini-icon mini-icon-create"></span>
          </a>
        </li>

        <li>
          <a href="/settings/profile" id="account_settings"
            class="tooltipped downwards"
            title="Account settings ">
            <span class="mini-icon mini-icon-account-settings"></span>
          </a>
        </li>
        <li>
          <a class="tooltipped downwards" href="/logout" data-method="post" id="logout" title="Sign out">
            <span class="mini-icon mini-icon-logout"></span>
          </a>
        </li>

    </ul>


<div class="js-new-dropdown-contents hidden">
  <ul class="dropdown-menu">
    <li>
      <a href="/new"><span class="mini-icon mini-icon-create"></span> New repository</a>
    </li>
    <li>
        <a href="https://github.com/ChessEP/Chess-Service/issues/new"><span class="mini-icon mini-icon-issue-opened"></span> New issue</a>
    </li>
    <li>
    </li>
    <li>
      <a href="/organizations/new"><span class="mini-icon mini-icon-u-list"></span> New organization</a>
    </li>
  </ul>
</div>


    
  </div>
</div>

      

      

      


            <div class="site hfeed" itemscope itemtype="http://schema.org/WebPage">
      <div class="hentry">
        
        <div class="pagehead repohead instapaper_ignore readability-menu ">
          <div class="container">
            <div class="title-actions-bar">
              

<ul class="pagehead-actions">

    <li class="nspr">
      <a href="/ChessEP/Chess-Service/pull/new/master" class="button minibutton btn-pull-request" icon_class="mini-icon-pull-request"><span class="mini-icon mini-icon-pull-request"></span>Pull Request</a>
    </li>

    <li class="subscription">
      <form accept-charset="UTF-8" action="/notifications/subscribe" data-autosubmit="true" data-remote="true" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="C23ZPCr39zqKQg8PjNG6sCBOeCpyhmhT+gLI1/UD2+A=" /></div>  <input id="repository_id" name="repository_id" type="hidden" value="7917498" />

    <div class="select-menu js-menu-container js-select-menu">
      <span class="minibutton select-menu-button js-menu-target">
        <span class="js-select-button">
          <span class="mini-icon mini-icon-unwatch"></span>
          Unwatch
        </span>
      </span>

      <div class="select-menu-modal-holder js-menu-content">
        <div class="select-menu-modal">
          <div class="select-menu-header">
            <span class="select-menu-title">Notification status</span>
            <span class="mini-icon mini-icon-remove-close js-menu-close"></span>
          </div> <!-- /.select-menu-header -->

          <div class="select-menu-list js-navigation-container">

            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
              <div class="select-menu-item-text">
                <input id="do_included" name="do" type="radio" value="included" />
                <h4>Not watching</h4>
                <span class="description">You only receive notifications for discussions in which you participate or are @mentioned.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="mini-icon mini-icon-watching"></span>
                  Watch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item selected">
              <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
              <div class="select-menu-item-text">
                <input checked="checked" id="do_subscribed" name="do" type="radio" value="subscribed" />
                <h4>Watching</h4>
                <span class="description">You receive notifications for all discussions in this repository.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="mini-icon mini-icon-unwatch"></span>
                  Unwatch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
              <div class="select-menu-item-text">
                <input id="do_ignore" name="do" type="radio" value="ignore" />
                <h4>Ignoring</h4>
                <span class="description">You do not receive any notifications for discussions in this repository.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="mini-icon mini-icon-mute"></span>
                  Stop ignoring
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

          </div> <!-- /.select-menu-list -->

        </div> <!-- /.select-menu-modal -->
      </div> <!-- /.select-menu-modal-holder -->
    </div> <!-- /.select-menu -->

</form>
    </li>

    <li class="js-toggler-container js-social-container starring-container ">
      <a href="/ChessEP/Chess-Service/unstar" class="minibutton js-toggler-target star-button starred upwards" title="Unstar this repo" data-remote="true" data-method="post" rel="nofollow">
        <span class="mini-icon mini-icon-remove-star"></span>
        <span class="text">Unstar</span>
      </a>
      <a href="/ChessEP/Chess-Service/star" class="minibutton js-toggler-target star-button unstarred upwards" title="Star this repo" data-remote="true" data-method="post" rel="nofollow">
        <span class="mini-icon mini-icon-star"></span>
        <span class="text">Star</span>
      </a>
      <a class="social-count js-social-count" href="/ChessEP/Chess-Service/stargazers">0</a>
    </li>

        <li>
          <a href="/ChessEP/Chess-Service/fork" class="minibutton js-toggler-target fork-button lighter upwards" title="Fork this repo" rel="nofollow" data-method="post">
            <span class="mini-icon mini-icon-branch-create"></span>
            <span class="text">Fork</span>
          </a>
          <a href="/ChessEP/Chess-Service/network" class="social-count">0</a>
        </li>


</ul>

              <h1 itemscope itemtype="http://data-vocabulary.org/Breadcrumb" class="entry-title public">
                <span class="repo-label"><span>public</span></span>
                <span class="mega-icon mega-icon-public-repo"></span>
                <span class="author vcard">
                  <a href="/ChessEP" class="url fn" itemprop="url" rel="author">
                  <span itemprop="title">ChessEP</span>
                  </a></span> /
                <strong><a href="/ChessEP/Chess-Service" class="js-current-repository">Chess-Service</a></strong>
              </h1>
            </div>

            
  <ul class="tabs">
    <li class="pulse-nav"><a href="/ChessEP/Chess-Service/pulse" class="js-selected-navigation-item " data-selected-links="pulse /ChessEP/Chess-Service/pulse" rel="nofollow"><span class="mini-icon mini-icon-pulse"></span></a></li>
    <li><a href="/ChessEP/Chess-Service" class="js-selected-navigation-item selected" data-selected-links="repo_source repo_downloads repo_commits repo_tags repo_branches /ChessEP/Chess-Service">Code</a></li>
    <li><a href="/ChessEP/Chess-Service/network" class="js-selected-navigation-item " data-selected-links="repo_network /ChessEP/Chess-Service/network">Network</a></li>
    <li><a href="/ChessEP/Chess-Service/pulls" class="js-selected-navigation-item " data-selected-links="repo_pulls /ChessEP/Chess-Service/pulls">Pull Requests <span class='counter'>0</span></a></li>

      <li><a href="/ChessEP/Chess-Service/issues" class="js-selected-navigation-item " data-selected-links="repo_issues /ChessEP/Chess-Service/issues">Issues <span class='counter'>0</span></a></li>

      <li><a href="/ChessEP/Chess-Service/wiki" class="js-selected-navigation-item " data-selected-links="repo_wiki /ChessEP/Chess-Service/wiki">Wiki</a></li>


    <li><a href="/ChessEP/Chess-Service/graphs" class="js-selected-navigation-item " data-selected-links="repo_graphs repo_contributors /ChessEP/Chess-Service/graphs">Graphs</a></li>


  </ul>
  
<div class="tabnav">

  <span class="tabnav-right">
    <ul class="tabnav-tabs">
          <li><a href="/ChessEP/Chess-Service/tags" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_tags /ChessEP/Chess-Service/tags">Tags <span class="counter blank">0</span></a></li>
    </ul>
  </span>

  <div class="tabnav-widget scope">


    <div class="select-menu js-menu-container js-select-menu js-branch-menu">
      <a class="minibutton select-menu-button js-menu-target" data-hotkey="w" data-ref="master">
        <span class="mini-icon mini-icon-branch"></span>
        <i>branch:</i>
        <span class="js-select-button">master</span>
      </a>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container">

        <div class="select-menu-modal">
          <div class="select-menu-header">
            <span class="select-menu-title">Switch branches/tags</span>
            <span class="mini-icon mini-icon-remove-close js-menu-close"></span>
          </div> <!-- /.select-menu-header -->

          <div class="select-menu-filters">
            <div class="select-menu-text-filter">
              <input type="text" id="commitish-filter-field" class="js-filterable-field js-navigation-enable" placeholder="Find or create a branch…">
            </div>
            <div class="select-menu-tabs">
              <ul>
                <li class="select-menu-tab">
                  <a href="#" data-tab-filter="branches" class="js-select-menu-tab">Branches</a>
                </li>
                <li class="select-menu-tab">
                  <a href="#" data-tab-filter="tags" class="js-select-menu-tab">Tags</a>
                </li>
              </ul>
            </div><!-- /.select-menu-tabs -->
          </div><!-- /.select-menu-filters -->

          <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket css-truncate" data-tab-filter="branches">

            <div data-filterable-for="commitish-filter-field" data-filterable-type="substring">

                <div class="select-menu-item js-navigation-item selected">
                  <span class="select-menu-item-icon mini-icon mini-icon-confirm"></span>
                  <a href="/ChessEP/Chess-Service/blob/master/CentralServer/src/core/Bot.java" class="js-navigation-open select-menu-item-text js-select-button-text css-truncate-target" data-name="master" rel="nofollow" title="master">master</a>
                </div> <!-- /.select-menu-item -->
            </div>

              <form accept-charset="UTF-8" action="/ChessEP/Chess-Service/branches" class="js-create-branch select-menu-item select-menu-new-item-form js-navigation-item js-new-item-form" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="C23ZPCr39zqKQg8PjNG6sCBOeCpyhmhT+gLI1/UD2+A=" /></div>

                <span class="mini-icon mini-icon-branch-create select-menu-item-icon"></span>
                <div class="select-menu-item-text">
                  <h4>Create branch: <span class="js-new-item-name"></span></h4>
                  <span class="description">from ‘master’</span>
                </div>
                <input type="hidden" name="name" id="name" class="js-new-item-value">
                <input type="hidden" name="branch" id="branch" value="master" />
                <input type="hidden" name="path" id="branch" value="CentralServer/src/core/Bot.java" />
              </form> <!-- /.select-menu-item -->

          </div> <!-- /.select-menu-list -->


          <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket css-truncate" data-tab-filter="tags">
            <div data-filterable-for="commitish-filter-field" data-filterable-type="substring">

            </div>

            <div class="select-menu-no-results">Nothing to show</div>

          </div> <!-- /.select-menu-list -->

        </div> <!-- /.select-menu-modal -->
      </div> <!-- /.select-menu-modal-holder -->
    </div> <!-- /.select-menu -->

  </div> <!-- /.scope -->

  <ul class="tabnav-tabs">
    <li><a href="/ChessEP/Chess-Service" class="selected js-selected-navigation-item tabnav-tab" data-selected-links="repo_source /ChessEP/Chess-Service">Files</a></li>
    <li><a href="/ChessEP/Chess-Service/commits/master" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_commits /ChessEP/Chess-Service/commits/master">Commits</a></li>
    <li><a href="/ChessEP/Chess-Service/branches" class="js-selected-navigation-item tabnav-tab" data-selected-links="repo_branches /ChessEP/Chess-Service/branches" rel="nofollow">Branches <span class="counter ">1</span></a></li>
  </ul>

</div>

  
  
  


            
          </div>
        </div><!-- /.repohead -->

        <div id="js-repo-pjax-container" class="container context-loader-container" data-pjax-container>
          


<!-- blob contrib key: blob_contributors:v21:a7ef73cf295172aa52ea3a767e756c62 -->
<!-- blob contrib frag key: views10/v8/blob_contributors:v21:a7ef73cf295172aa52ea3a767e756c62 -->


<div id="slider">
    <div class="frame-meta">

      <p title="This is a placeholder element" class="js-history-link-replace hidden"></p>

        <div class="breadcrumb">
          <span class='bold'><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/ChessEP/Chess-Service" class="js-slide-to" data-branch="master" data-direction="back" itemscope="url"><span itemprop="title">Chess-Service</span></a></span></span><span class="separator"> / </span><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/ChessEP/Chess-Service/tree/master/CentralServer" class="js-slide-to" data-branch="master" data-direction="back" itemscope="url"><span itemprop="title">CentralServer</span></a></span><span class="separator"> / </span><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/ChessEP/Chess-Service/tree/master/CentralServer/src" class="js-slide-to" data-branch="master" data-direction="back" itemscope="url"><span itemprop="title">src</span></a></span><span class="separator"> / </span><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/ChessEP/Chess-Service/tree/master/CentralServer/src/core" class="js-slide-to" data-branch="master" data-direction="back" itemscope="url"><span itemprop="title">core</span></a></span><span class="separator"> / </span><strong class="final-path">Bot.java</strong> <span class="js-zeroclipboard zeroclipboard-button" data-clipboard-text="CentralServer/src/core/Bot.java" data-copied-hint="copied!" title="copy to clipboard"><span class="mini-icon mini-icon-clipboard"></span></span>
        </div>

      <a href="/ChessEP/Chess-Service/find/master" class="js-slide-to" data-hotkey="t" style="display:none">Show File Finder</a>


        <div class="commit commit-loader file-history-tease js-deferred-content" data-url="/ChessEP/Chess-Service/contributors/master/CentralServer/src/core/Bot.java">
          Fetching contributors…

          <div class="participation">
            <p class="loader-loading"><img alt="Octocat-spinner-32-eaf2f5" height="16" src="https://a248.e.akamai.net/assets.github.com/images/spinners/octocat-spinner-32-EAF2F5.gif?1360648847" width="16" /></p>
            <p class="loader-error">Cannot retrieve contributors at this time</p>
          </div>
        </div>

    </div><!-- ./.frame-meta -->

    <div class="frames">
      <div class="frame" data-permalink-url="/ChessEP/Chess-Service/blob/0452fb880ee9787ca670d2d2a137a8881f3d5673/CentralServer/src/core/Bot.java" data-title="Chess-Service/CentralServer/src/core/Bot.java at master · ChessEP/Chess-Service · GitHub" data-type="blob">

        <div id="files" class="bubble">
          <div class="file">
            <div class="meta">
              <div class="info">
                <span class="icon"><b class="mini-icon mini-icon-text-file"></b></span>
                <span class="mode" title="File Mode">file</span>
                  <span>43 lines (36 sloc)</span>
                <span>0.921 kb</span>
              </div>
              <div class="actions">
                <div class="button-group">
                        <a class="minibutton"
                           href="/ChessEP/Chess-Service/edit/master/CentralServer/src/core/Bot.java"
                           data-method="post" rel="nofollow" data-hotkey="e">Edit</a>
                  <a href="/ChessEP/Chess-Service/raw/master/CentralServer/src/core/Bot.java" class="button minibutton " id="raw-url">Raw</a>
                    <a href="/ChessEP/Chess-Service/blame/master/CentralServer/src/core/Bot.java" class="button minibutton ">Blame</a>
                  <a href="/ChessEP/Chess-Service/commits/master/CentralServer/src/core/Bot.java" class="button minibutton " rel="nofollow">History</a>
                </div><!-- /.button-group -->
              </div><!-- /.actions -->

            </div>
                <div class="blob-wrapper data type-java js-blob-data">
      <table class="file-code file-diff">
        <tr class="file-code-line">
          <td class="blob-line-nums">
            <span id="L1" rel="#L1">1</span>
<span id="L2" rel="#L2">2</span>
<span id="L3" rel="#L3">3</span>
<span id="L4" rel="#L4">4</span>
<span id="L5" rel="#L5">5</span>
<span id="L6" rel="#L6">6</span>
<span id="L7" rel="#L7">7</span>
<span id="L8" rel="#L8">8</span>
<span id="L9" rel="#L9">9</span>
<span id="L10" rel="#L10">10</span>
<span id="L11" rel="#L11">11</span>
<span id="L12" rel="#L12">12</span>
<span id="L13" rel="#L13">13</span>
<span id="L14" rel="#L14">14</span>
<span id="L15" rel="#L15">15</span>
<span id="L16" rel="#L16">16</span>
<span id="L17" rel="#L17">17</span>
<span id="L18" rel="#L18">18</span>
<span id="L19" rel="#L19">19</span>
<span id="L20" rel="#L20">20</span>
<span id="L21" rel="#L21">21</span>
<span id="L22" rel="#L22">22</span>
<span id="L23" rel="#L23">23</span>
<span id="L24" rel="#L24">24</span>
<span id="L25" rel="#L25">25</span>
<span id="L26" rel="#L26">26</span>
<span id="L27" rel="#L27">27</span>
<span id="L28" rel="#L28">28</span>
<span id="L29" rel="#L29">29</span>
<span id="L30" rel="#L30">30</span>
<span id="L31" rel="#L31">31</span>
<span id="L32" rel="#L32">32</span>
<span id="L33" rel="#L33">33</span>
<span id="L34" rel="#L34">34</span>
<span id="L35" rel="#L35">35</span>
<span id="L36" rel="#L36">36</span>
<span id="L37" rel="#L37">37</span>
<span id="L38" rel="#L38">38</span>
<span id="L39" rel="#L39">39</span>
<span id="L40" rel="#L40">40</span>
<span id="L41" rel="#L41">41</span>
<span id="L42" rel="#L42">42</span>
<span id="L43" rel="#L43">43</span>

          </td>
          <td class="blob-line-code">
                  <div class="highlight"><pre><div class='line' id='LC1'><span class="kn">package</span> <span class="n">core</span><span class="o">;</span></div><div class='line' id='LC2'><br/></div><div class='line' id='LC3'><span class="kn">import</span> <span class="nn">java.util.LinkedList</span><span class="o">;</span></div><div class='line' id='LC4'><span class="kn">import</span> <span class="nn">java.util.List</span><span class="o">;</span></div><div class='line' id='LC5'><br/></div><div class='line' id='LC6'><span class="kn">import</span> <span class="nn">org.json.JSONObject</span><span class="o">;</span></div><div class='line' id='LC7'><br/></div><div class='line' id='LC8'><span class="cm">/**</span></div><div class='line' id='LC9'><span class="cm"> * Represent a bot.</span></div><div class='line' id='LC10'><span class="cm"> * @author Paul Chaignon</span></div><div class='line' id='LC11'><span class="cm"> */</span></div><div class='line' id='LC12'><span class="kd">public</span> <span class="kd">class</span> <span class="nc">Bot</span> <span class="kd">extends</span> <span class="n">Resource</span> <span class="o">{</span></div><div class='line' id='LC13'>	<span class="kd">private</span> <span class="n">List</span><span class="o">&lt;</span><span class="n">BotSuggestion</span><span class="o">&gt;</span> <span class="n">moves</span><span class="o">;</span></div><div class='line' id='LC14'><br/></div><div class='line' id='LC15'>	<span class="cm">/**</span></div><div class='line' id='LC16'><span class="cm">	 * Constructor</span></div><div class='line' id='LC17'><span class="cm">	 * @param uri The URI.</span></div><div class='line' id='LC18'><span class="cm">	 * @param name The name.</span></div><div class='line' id='LC19'><span class="cm">	 * @param trust The trust in this bot.</span></div><div class='line' id='LC20'><span class="cm">	 */</span></div><div class='line' id='LC21'>	<span class="kd">public</span> <span class="nf">Bot</span><span class="o">(</span><span class="n">String</span> <span class="n">uri</span><span class="o">,</span> <span class="n">String</span> <span class="n">name</span><span class="o">,</span> <span class="kt">int</span> <span class="n">trust</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC22'>		<span class="kd">super</span><span class="o">(</span><span class="n">uri</span><span class="o">,</span> <span class="n">name</span><span class="o">,</span> <span class="n">trust</span><span class="o">);</span></div><div class='line' id='LC23'>		<span class="k">this</span><span class="o">.</span><span class="na">moves</span> <span class="o">=</span> <span class="k">new</span> <span class="n">LinkedList</span><span class="o">&lt;</span><span class="n">BotSuggestion</span><span class="o">&gt;();</span></div><div class='line' id='LC24'>	<span class="o">}</span></div><div class='line' id='LC25'><br/></div><div class='line' id='LC26'>	<span class="nd">@Override</span></div><div class='line' id='LC27'>	<span class="kd">public</span> <span class="n">List</span><span class="o">&lt;</span><span class="n">BotSuggestion</span><span class="o">&gt;</span> <span class="nf">getMoveSuggestions</span><span class="o">()</span> <span class="o">{</span></div><div class='line' id='LC28'>		<span class="k">return</span> <span class="k">this</span><span class="o">.</span><span class="na">moves</span><span class="o">;</span></div><div class='line' id='LC29'>	<span class="o">}</span></div><div class='line' id='LC30'><br/></div><div class='line' id='LC31'>	<span class="nd">@Override</span></div><div class='line' id='LC32'>	<span class="kd">protected</span> <span class="kt">void</span> <span class="nf">clearSuggestions</span><span class="o">()</span> <span class="o">{</span></div><div class='line' id='LC33'>		<span class="k">this</span><span class="o">.</span><span class="na">moves</span><span class="o">.</span><span class="na">clear</span><span class="o">();</span></div><div class='line' id='LC34'>	<span class="o">}</span></div><div class='line' id='LC35'><br/></div><div class='line' id='LC36'>	<span class="nd">@Override</span></div><div class='line' id='LC37'>	<span class="kd">protected</span> <span class="kt">void</span> <span class="nf">parseJSONMove</span><span class="o">(</span><span class="n">String</span> <span class="n">response</span><span class="o">,</span> <span class="n">String</span> <span class="n">fen</span><span class="o">)</span> <span class="o">{</span></div><div class='line' id='LC38'>		<span class="n">JSONObject</span> <span class="n">json</span> <span class="o">=</span> <span class="k">new</span> <span class="n">JSONObject</span><span class="o">(</span><span class="n">response</span><span class="o">);</span></div><div class='line' id='LC39'>		<span class="n">String</span> <span class="n">move</span> <span class="o">=</span> <span class="n">json</span><span class="o">.</span><span class="na">getString</span><span class="o">(</span><span class="n">JSON_MOVE</span><span class="o">);</span></div><div class='line' id='LC40'>		<span class="n">BotSuggestion</span> <span class="n">suggestion</span> <span class="o">=</span> <span class="k">new</span> <span class="n">BotSuggestion</span><span class="o">(</span><span class="n">move</span><span class="o">);</span></div><div class='line' id='LC41'>		<span class="k">this</span><span class="o">.</span><span class="na">moves</span><span class="o">.</span><span class="na">add</span><span class="o">(</span><span class="n">suggestion</span><span class="o">);</span></div><div class='line' id='LC42'>	<span class="o">}</span></div><div class='line' id='LC43'><span class="o">}</span></div></pre></div>
          </td>
        </tr>
      </table>
  </div>

          </div>
        </div>

        <a href="#jump-to-line" rel="facebox" data-hotkey="l" class="js-jump-to-line" style="display:none">Jump to Line</a>
        <div id="jump-to-line" style="display:none">
          <h2>Jump to Line</h2>
          <form accept-charset="UTF-8" class="js-jump-to-line-form">
            <input class="textfield js-jump-to-line-field" type="text">
            <div class="full-button">
              <button type="submit" class="button">Go</button>
            </div>
          </form>
        </div>

      </div>
    </div>
</div>

<div id="js-frame-loading-template" class="frame frame-loading large-loading-area" style="display:none;">
  <img class="js-frame-loading-spinner" src="https://a248.e.akamai.net/assets.github.com/images/spinners/octocat-spinner-128.gif?1360648847" height="64" width="64">
</div>


        </div>
      </div>
      <div class="context-overlay"></div>
    </div>

      <div id="footer-push"></div><!-- hack for sticky footer -->
    </div><!-- end of wrapper - hack for sticky footer -->

      <!-- footer -->
      <div id="footer">
  <div class="container clearfix">

      <dl class="footer_nav">
        <dt>GitHub</dt>
        <dd><a href="https://github.com/about">About us</a></dd>
        <dd><a href="https://github.com/blog">Blog</a></dd>
        <dd><a href="https://github.com/contact">Contact &amp; support</a></dd>
        <dd><a href="http://enterprise.github.com/">GitHub Enterprise</a></dd>
        <dd><a href="http://status.github.com/">Site status</a></dd>
      </dl>

      <dl class="footer_nav">
        <dt>Applications</dt>
        <dd><a href="http://mac.github.com/">GitHub for Mac</a></dd>
        <dd><a href="http://windows.github.com/">GitHub for Windows</a></dd>
        <dd><a href="http://eclipse.github.com/">GitHub for Eclipse</a></dd>
        <dd><a href="http://mobile.github.com/">GitHub mobile apps</a></dd>
      </dl>

      <dl class="footer_nav">
        <dt>Services</dt>
        <dd><a href="http://get.gaug.es/">Gauges: Web analytics</a></dd>
        <dd><a href="http://speakerdeck.com">Speaker Deck: Presentations</a></dd>
        <dd><a href="https://gist.github.com">Gist: Code snippets</a></dd>
        <dd><a href="http://jobs.github.com/">Job board</a></dd>
      </dl>

      <dl class="footer_nav">
        <dt>Documentation</dt>
        <dd><a href="http://help.github.com/">GitHub Help</a></dd>
        <dd><a href="http://developer.github.com/">Developer API</a></dd>
        <dd><a href="http://github.github.com/github-flavored-markdown/">GitHub Flavored Markdown</a></dd>
        <dd><a href="http://pages.github.com/">GitHub Pages</a></dd>
      </dl>

      <dl class="footer_nav">
        <dt>More</dt>
        <dd><a href="http://training.github.com/">Training</a></dd>
        <dd><a href="https://github.com/edu">Students &amp; teachers</a></dd>
        <dd><a href="http://shop.github.com">The Shop</a></dd>
        <dd><a href="/plans">Plans &amp; pricing</a></dd>
        <dd><a href="http://octodex.github.com/">The Octodex</a></dd>
      </dl>

      <hr class="footer-divider">


    <p class="right">&copy; 2013 <span title="0.10411s from fe4.rs.github.com">GitHub</span>, Inc. All rights reserved.</p>
    <a class="left" href="https://github.com/">
      <span class="mega-icon mega-icon-invertocat"></span>
    </a>
    <ul id="legal">
        <li><a href="https://github.com/site/terms">Terms of Service</a></li>
        <li><a href="https://github.com/site/privacy">Privacy</a></li>
        <li><a href="https://github.com/security">Security</a></li>
    </ul>

  </div><!-- /.container -->

</div><!-- /.#footer -->


    <div class="fullscreen-overlay js-fullscreen-overlay" id="fullscreen_overlay">
  <div class="fullscreen-container js-fullscreen-container">
    <div class="textarea-wrap">
      <textarea name="fullscreen-contents" id="fullscreen-contents" class="js-fullscreen-contents" placeholder="" data-suggester="fullscreen_suggester"></textarea>
          <div class="suggester-container">
              <div class="suggester fullscreen-suggester js-navigation-container" id="fullscreen_suggester"
                 data-url="/ChessEP/Chess-Service/suggestions/commit">
              </div>
          </div>
    </div>
  </div>
  <div class="fullscreen-sidebar">
    <a href="#" class="exit-fullscreen js-exit-fullscreen tooltipped leftwards" title="Exit Zen Mode">
      <span class="mega-icon mega-icon-normalscreen"></span>
    </a>
    <a href="#" class="theme-switcher js-theme-switcher tooltipped leftwards"
      title="Switch themes">
      <span class="mini-icon mini-icon-brightness"></span>
    </a>
  </div>
</div>



    <div id="ajax-error-message" class="flash flash-error">
      <span class="mini-icon mini-icon-exclamation"></span>
      Something went wrong with that request. Please try again.
      <a href="#" class="mini-icon mini-icon-remove-close ajax-error-dismiss"></a>
    </div>

    
    
    <span id='server_response_time' data-time='0.10465' data-host='fe4'></span>
    
  </body>
</html>

