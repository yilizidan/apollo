/*! LAB.js (LABjs :: Loading And Blocking JavaScript)
    v2.0.3 (c) Kyle Simpson
    MIT License
*/
/*jac: 2016.5.13 在v2.0.3的基础上增加动态加载CSS文件功能，支持加载完成时候的回调（成功 and 失败 情况下）
  源码实现借鉴：https://github.com/rgrove/lazyload/commit/6caf58525532ee8046c78a1b026f066bad46d32d
  更多关于CSS加载的坑的讨论，见：http://www.phpied.com/when-is-a-stylesheet-really-loaded/

  1. 所有浏览器对onerror的支持都不完美，所以只是尽量处理
  2. IE浏览器/firefox9.0级以上/opera/chrome浏览器20及以上/safari浏览器6.0以上都支持css的onload事件，因此通过监听onload即可。
  3. chrome浏览器9.10到19.0/safari浏览器5.0到5.9/firefox浏览器8.9一下则通过img的onerror事件即可模拟出css文件的加载成功事件
  4. 其他浏览器,比如firefox<7和一些低版本的webkit内核, chrome浏览器9.0及以下则只能通过轮询css样式节点是否附加成功来判断了

  更多说明：目前只能判断CSS文件加载事件是否完成，至于是否出现404、5XX等，还判断不了
  曲线救国：回调里判断CSS里定义的某个样式是否存在/生效，借此判断CSS是否下载成功，如下
 .wait(function(){
    var div = document.createElement('div');
    div.className = 'pre_defined_class';  //pre_defined_class 为测试用的预定义类，假设为 .pre_defined_class{display:none;}
    var value = getStyle(div, 'display');
    if(value=='none'){
      //成功
    }else{
      //失败
    }
  })
*/
(function (global) {
    function detect(ua, platform) {
        var os = global.$os = {}, browser = global.$browser = {},
            webkit = ua.match(/Web[kK]it[\/]{0,1}([\d.]+)/),
            android = ua.match(/(Android);?[\s\/]+([\d.]+)?/),
            osx = !!ua.match(/\(Macintosh\; Intel /),
            ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
            ipod = ua.match(/(iPod)(.*OS\s([\d_]+))?/),
            iphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
            webos = ua.match(/(webOS|hpwOS)[\s\/]([\d.]+)/),
            win = /Win\d{2}|Windows/.test(platform),
            wp = ua.match(/Windows Phone ([\d.]+)/),
            touchpad = webos && ua.match(/TouchPad/),
            kindle = ua.match(/Kindle\/([\d.]+)/),
            silk = ua.match(/Silk\/([\d._]+)/),
            blackberry = ua.match(/(BlackBerry).*Version\/([\d.]+)/),
            bb10 = ua.match(/(BB10).*Version\/([\d.]+)/),
            rimtabletos = ua.match(/(RIM\sTablet\sOS)\s([\d.]+)/),
            playbook = ua.match(/PlayBook/),
            opera = ua.match(/Opera/),
            chrome = ua.match(/Chrome\/([\d.]+)/) || ua.match(/CriOS\/([\d.]+)/),
            firefox = ua.match(/Firefox\/([\d.]+)/),
            firefoxos = ua.match(/\((?:Mobile|Tablet); rv:([\d.]+)\).*Firefox\/[\d.]+/),
            ie = ua.match(/MSIE\s([\d.]+)/) || ua.match(/Trident\/[\d](?=[^\?]+).*rv:([0-9.].)/),
            webview = !chrome && ua.match(/(iPhone|iPod|iPad).*AppleWebKit(?!.*Safari)/),
            safari = webview || ua.match(/Version\/([\d.]+)([^S](Safari)|[^M]*(Mobile)[^S]*(Safari))/)

        // Todo: clean this up with a better OS/browser seperation:
        // - discern (more) between multiple browsers on android
        // - decide if kindle fire in silk mode is android or not
        // - Firefox on Android doesn't specify the Android version
        // - possibly devide in os, device and browser hashes

        if (browser.webkit = !!webkit) browser.version = webkit[1]

        if (android) os.android = true, os.version = android[2]
        if (iphone && !ipod) os.ios = os.iphone = true, os.version = iphone[2].replace(/_/g, '.')
        if (ipad) os.ios = os.ipad = true, os.version = ipad[2].replace(/_/g, '.')
        if (ipod) os.ios = os.ipod = true, os.version = ipod[3] ? ipod[3].replace(/_/g, '.') : null
        if (wp) os.wp = true, os.version = wp[1]
        if (webos) os.webos = true, os.version = webos[2]
        if (touchpad) os.touchpad = true
        if (blackberry) os.blackberry = true, os.version = blackberry[2]
        if (bb10) os.bb10 = true, os.version = bb10[2]
        if (rimtabletos) os.rimtabletos = true, os.version = rimtabletos[2]
        if (playbook) browser.playbook = true
        if (kindle) os.kindle = true, os.version = kindle[1]
        if (silk) browser.silk = true, browser.version = silk[1]
        if (!silk && os.android && ua.match(/Kindle Fire/)) browser.silk = true
        if (opera) browser.opera = true
        if (chrome) browser.chrome = true, browser.version = chrome[1]
        if (firefox) browser.firefox = true, browser.version = firefox[1]
        if (firefoxos) os.firefoxos = true, os.version = firefoxos[1]
        if (ie) browser.ie = true, browser.version = ie[1]
        if (safari && (osx || os.ios || win)) {
            browser.safari = true
            //if (!os.ios) browser.version = safari[1]
            if (safari.length > 1) browser.version = safari[1]
        }
        if (webview) browser.webview = true

        os.tablet = !!(ipad || playbook || (android && !ua.match(/Mobile/)) ||
            (firefox && ua.match(/Tablet/)) || (ie && !ua.match(/Phone/) && ua.match(/Touch/)))
        os.phone = !!(!os.tablet && !os.ipod && (android || iphone || webos || blackberry || bb10 ||
            (chrome && ua.match(/Android/)) || (chrome && ua.match(/CriOS\/([\d.]+)/)) ||
            (firefox && ua.match(/Mobile/)) || (ie && ua.match(/Touch/))))
    }

    detect.call(global, navigator.userAgent, navigator.platform)
    // make available to unit tests
    //$.__detect = detect
})(window);

(function (global) {

    /*
    var styleSheets = document.styleSheets;
    var env = getEnv(); //获取用户代理信息，为浏览器差异化加载提供判断依据
    function getEnv() {
      var ua = navigator.userAgent;
      var env = {};

      (env.webkit = /AppleWebKit\//.test(ua))
        || (env.ie = /MSIE/.test(ua))
        || (env.opera = /Opera/.test(ua))
        || (env.gecko = /Gecko\//.test(ua))
        || (env.unknown = true);

      return env;
    }*/

    //load js
    var _$LAB = global.$LAB,

        // constants for the valid keys of the options object
        _UseLocalXHR = "UseLocalXHR",
        _AlwaysPreserveOrder = "AlwaysPreserveOrder",
        _AllowDuplicates = "AllowDuplicates",
        _CacheBust = "CacheBust",
        _Debug = "Debug",
        _BasePath = "BasePath",

        // stateless variables used across all $LAB instances
        root_page = /^[^?#]*\//.exec(location.href)[0],
        root_domain = /^\w+\:\/\/\/?[^\/]+/.exec(root_page)[0],
        append_to = document.head || document.getElementsByTagName("head"),

        // inferences... ick, but still necessary
        opera_or_gecko = (global.opera && Object.prototype.toString.call(global.opera) == "[object Opera]") || ("MozAppearance" in document.documentElement.style),

        log_msg = function () {
        },
        log_error = log_msg,

        // feature sniffs (yay!)
        test_script_elem = document.createElement("script"),
        explicit_preloading = typeof test_script_elem.preload == "boolean", // http://wiki.whatwg.org/wiki/Script_Execution_Control#Proposal_1_.28Nicholas_Zakas.29
        real_preloading = explicit_preloading || (test_script_elem.readyState && test_script_elem.readyState == "uninitialized"), // will a script preload with `src` set before DOM append?
        script_ordered_async = !real_preloading && test_script_elem.async === true, // http://wiki.whatwg.org/wiki/Dynamic_Script_Execution_Order

        // XHR preloading (same-domain) and cache-preloading (remote-domain) are the fallbacks (for some browsers)
        xhr_or_cache_preloading = !real_preloading && !script_ordered_async && !opera_or_gecko
    ;

    /*!START_DEBUG*/
    // define console wrapper functions if applicable
    if (global.console && global.console.log) {
        if (!global.console.error) global.console.error = global.console.log;
        log_msg = function (msg) {
            global.console.log(msg);
        };
        log_error = function (msg, err) {
            global.console.error(msg, err);
        };
    }
    /*!END_DEBUG*/

    // test for function
    function is_func(func) {
        return Object.prototype.toString.call(func) == "[object Function]";
    }

    // test for array
    function is_array(arr) {
        return Object.prototype.toString.call(arr) == "[object Array]";
    }

    // make script URL absolute/canonical
    function canonical_uri(src, base_path) {
        var absolute_regex = /^\w+\:\/\//;

        // is `src` is protocol-relative (begins with // or ///), prepend protocol
        if (/^\/\/\/?/.test(src)) {
            src = location.protocol + src;
        }
        // is `src` page-relative? (not an absolute URL, and not a domain-relative path, beginning with /)
        else if (!absolute_regex.test(src) && src.charAt(0) != "/") {
            // prepend `base_path`, if any
            src = (base_path || "") + src;
        }
        // make sure to return `src` as absolute
        return absolute_regex.test(src) ? src : ((src.charAt(0) == "/" ? root_domain : root_page) + src);
    }

    // merge `source` into `target`
    function merge_objs(source, target) {
        for (var k in source) {
            if (source.hasOwnProperty(k)) {
                target[k] = source[k]; // TODO: does this need to be recursive for our purposes?
            }
        }
        return target;
    }

    // does the chain group have any ready-to-execute scripts?
    function check_chain_group_scripts_ready(chain_group) {
        var any_scripts_ready = false;
        for (var i = 0; i < chain_group.scripts.length; i++) {
            if (chain_group.scripts[i].ready && chain_group.scripts[i].exec_trigger) {
                any_scripts_ready = true;
                chain_group.scripts[i].exec_trigger();
                chain_group.scripts[i].exec_trigger = null;
            }
        }
        return any_scripts_ready;
    }

    // creates a script load listener
    function create_script_load_listener(elem, registry_item, flag, onload) {
        elem.onload = elem.onreadystatechange = function () {
            if ((elem.readyState && elem.readyState != "complete" && elem.readyState != "loaded") || registry_item[flag]) return;
            elem.onload = elem.onreadystatechange = null;
            onload();
        };
    }

    // script executed handler
    function script_executed(registry_item) {
        registry_item.ready = registry_item.finished = true;
        for (var i = 0; i < registry_item.finished_listeners.length; i++) {
            registry_item.finished_listeners[i]();
        }
        registry_item.ready_listeners = [];
        registry_item.finished_listeners = [];
    }

    // make the request for a scriptha
    function request_script(chain_opts, script_obj, registry_item, onload, preload_this_script) {
        // setTimeout() "yielding" prevents some weird race/crash conditions in older browsers
        setTimeout(function () {
            var script, src = script_obj.real_src, xhr;

            // don't proceed until `append_to` is ready to append to
            if ("item" in append_to) { // check if `append_to` ref is still a live node list
                if (!append_to[0]) { // `append_to` node not yet ready
                    // try again in a little bit -- note: will re-call the anonymous function in the outer setTimeout, not the parent `request_script()`
                    setTimeout(arguments.callee, 25);
                    return;
                }
                // reassign from live node list ref to pure node ref -- avoids nasty IE bug where changes to DOM invalidate live node lists
                append_to = append_to[0];
            }
            script = document.createElement("script");
            if (script_obj.type) script.type = script_obj.type;
            if (script_obj.charset) script.charset = script_obj.charset;

            // should preloading be used for this script?
            if (preload_this_script) {
                // real script preloading?
                if (real_preloading) {
                    /*!START_DEBUG*/
                    if (chain_opts[_Debug]) log_msg("start script preload: " + src);
                    /*!END_DEBUG*/
                    registry_item.elem = script;
                    if (explicit_preloading) { // explicit preloading (aka, Zakas' proposal)
                        script.preload = true;
                        script.onpreload = onload;
                    }
                    else {
                        script.onreadystatechange = function () {
                            if (script.readyState == "loaded") onload();
                        };
                    }
                    script.src = src;
                    // NOTE: no append to DOM yet, appending will happen when ready to execute
                }
                // same-domain and XHR allowed? use XHR preloading
                else if (preload_this_script && src.indexOf(root_domain) == 0 && chain_opts[_UseLocalXHR]) {
                    xhr = new XMLHttpRequest(); // note: IE never uses XHR (it supports true preloading), so no more need for ActiveXObject fallback for IE <= 7
                    /*!START_DEBUG*/
                    if (chain_opts[_Debug]) log_msg("start script preload (xhr): " + src);
                    /*!END_DEBUG*/
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4) {
                            xhr.onreadystatechange = function () {
                            }; // fix a memory leak in IE
                            registry_item.text = xhr.responseText + "\n//@ sourceURL=" + src; // http://blog.getfirebug.com/2009/08/11/give-your-eval-a-name-with-sourceurl/
                            onload();
                        }
                    };
                    xhr.open("GET", src);
                    xhr.send();
                }
                // as a last resort, use cache-preloading
                else {
                    /*!START_DEBUG*/
                    if (chain_opts[_Debug]) log_msg("start script preload (cache): " + src);
                    /*!END_DEBUG*/
                    script.type = "text/cache-script";
                    create_script_load_listener(script, registry_item, "ready", function () {
                        append_to.removeChild(script);
                        onload();
                    });
                    script.src = src;
                    append_to.appendChild(script);
                    //append_to.insertBefore(script,append_to.firstChild);
                }
            }
            // use async=false for ordered async? parallel-load-serial-execute http://wiki.whatwg.org/wiki/Dynamic_Script_Execution_Order
            else if (script_ordered_async) {
                /*!START_DEBUG*/
                if (chain_opts[_Debug]) log_msg("start script load (ordered async): " + src);
                /*!END_DEBUG*/
                script.async = false;
                create_script_load_listener(script, registry_item, "finished", onload);
                script.src = src;
                append_to.appendChild(script);
                //append_to.insertBefore(script,append_to.firstChild);
            }
            // otherwise, just a normal script element
            else {
                /*!START_DEBUG*/
                if (chain_opts[_Debug]) log_msg("start script load: " + src);
                /*!END_DEBUG*/
                create_script_load_listener(script, registry_item, "finished", onload);
                script.src = src;
                append_to.appendChild(script);
                //append_to.insertBefore(script,append_to.firstChild);
            }
        }, 0);
    }

    //加载CSS文件 2016.5.13 by jac
    function request_css(chain_opts, script_obj, registry_item, onload, preload_this_script) {
        // setTimeout() "yielding" prevents some weird race/crash conditions in older browsers
        setTimeout(function () {
            var link, url = script_obj.real_src;
            // don't proceed until `append_to` is ready to append to
            if ("item" in append_to) { // check if `append_to` ref is still a live node list
                if (!append_to[0]) { // `append_to` node not yet ready
                    // try again in a little bit -- note: will re-call the anonymous function in the outer setTimeout, not the parent `request_css()`
                    setTimeout(arguments.callee, 25);
                    return;
                }
                // reassign from live node list ref to pure node ref -- avoids nasty IE bug where changes to DOM invalidate live node lists
                append_to = append_to[0];
            }
            //成功之后做的事情
            var wellDone = function () {
                link.onload = link.onerror = null;
                onload();
            };

            link = document.createElement("link");
            link.rel = "stylesheet";
            link.type = "text/css";
            link.href = url;
            if (script_obj.charset)
                link.setAttribute('charset', script_obj.charset);
            link.onerror = function () {
                link.onload = link.onerror = null;
                /*!START_DEBUG*/
                if (chain_opts[_Debug]) log_msg("load css file error: " + url);
                /*!END_DEBUG*/
            };
            var bsVer = null;
            try {
                bsVer = parseFloat($browser.version)
            } catch (e) {
            }
            //如果是IE系列,直接load事件
            if ($browser.ie
                || ($browser.firefox && bsVer > 8.9)
                || $browser.opera
                || ($browser.chrome && bsVer > 19)
                || ($browser.safari && bsVer > 5.9)
            ) {
                //IE和opera浏览器用img实现
                link.onload = function () {
                    wellDone()
                };
                append_to.appendChild(link);
                //append_to.insertBefore(link,append_to.firstChild);
            } else if (
                ($browser.chrome && bsVer > 9)
                || ($browser.safari && bsVer > 4.9)
                || $browser.firefox
            ) {
                append_to.appendChild(link);
                //append_to.insertBefore(link,append_to.firstChild);
                //如果是非IE系列
                var img = document.createElement('img');
                img.onerror = function () {
                    img.onerror = null;
                    img = null;
                    wellDone();
                };
                img.src = url;
            } else {//轮询实现
                append_to.appendChild(link);
                //append_to.insertBefore(link, append_to.firstChild);
                link.pollCount = 0;
                var poll = function () {
                    link.pollCount++; //轮询次数加1
                    if (link.sheet && link.sheet.cssRules) {
                        wellDone();
                    } else if (link.pollCount < 100) {
                        setTimeout(poll, 100);
                    }
                };
                poll();
            }

        }, 0);
    }

    // create a clean instance of $LAB
    function create_sandbox() {
        var global_defaults = {},
            can_use_preloading = real_preloading || xhr_or_cache_preloading,
            queue = [],
            registry = {},
            instanceAPI
        ;

        // global defaults
        global_defaults[_UseLocalXHR] = true;
        global_defaults[_AlwaysPreserveOrder] = false;
        global_defaults[_AllowDuplicates] = false;
        global_defaults[_CacheBust] = false;
        /*!START_DEBUG*/
        global_defaults[_Debug] = false;
        /*!END_DEBUG*/
        global_defaults[_BasePath] = "";

        // execute a script that has been preloaded already
        function execute_preloaded_script(chain_opts, script_obj, registry_item) {
            var script;

            function preload_execute_finished() {
                if (script != null) { // make sure this only ever fires once
                    script = null;
                    script_executed(registry_item);
                }
            }

            if (registry[script_obj.src].finished) return;
            if (!chain_opts[_AllowDuplicates]) registry[script_obj.src].finished = true;

            script = registry_item.elem || document.createElement("script");
            if (script_obj.type) script.type = script_obj.type;
            if (script_obj.charset) script.charset = script_obj.charset;
            create_script_load_listener(script, registry_item, "finished", preload_execute_finished);

            // script elem was real-preloaded
            if (registry_item.elem) {
                registry_item.elem = null;
            }
            // script was XHR preloaded
            else if (registry_item.text) {
                script.onload = script.onreadystatechange = null;	// script injection doesn't fire these events
                script.text = registry_item.text;
            }
            // script was cache-preloaded
            else {
                script.src = script_obj.real_src;
            }
            append_to.appendChild(script);
            //append_to.insertBefore(script,append_to.firstChild);

            // manually fire execution callback for injected scripts, since events don't fire
            if (registry_item.text) {
                preload_execute_finished();
            }
        }

        // process the script request setup
        function do_script(chain_opts, script_obj, chain_group, preload_this_script) {
            var registry_item,
                registry_items,
                ready_cb = function () {
                    script_obj.ready_cb(script_obj, function () {
                        execute_preloaded_script(chain_opts, script_obj, registry_item);
                    });
                },
                finished_cb = function () {
                    script_obj.finished_cb(script_obj, chain_group);
                }
            ;
            /*if(script_obj.css == undefined){
              var src = script_obj.src;
              script_obj.css = src.lastIndexOf('.css') > 0;
            }*/

            script_obj.src = canonical_uri(script_obj.src, chain_opts[_BasePath]);
            script_obj.real_src = script_obj.src +
                // append cache-bust param to URL?
                (chain_opts[_CacheBust] ? ((/\?.*$/.test(script_obj.src) ? "&_" : "?_") + ~~(Math.random() * 1E9) + "=") : "")
            ;

            if (!registry[script_obj.src]) registry[script_obj.src] = {items: [], finished: false};
            registry_items = registry[script_obj.src].items;

            // allowing duplicates, or is this the first recorded load of this script?
            if (chain_opts[_AllowDuplicates] || registry_items.length == 0) {
                registry_item = registry_items[registry_items.length] = {
                    ready: false,
                    finished: false,
                    ready_listeners: [ready_cb],
                    finished_listeners: [finished_cb]
                };
                if (script_obj.type === 'css')
                    request_css(chain_opts, script_obj, registry_item, function () {
                        script_executed(registry_item);
                    }, preload_this_script);
                else
                    request_script(chain_opts, script_obj, registry_item,
                        // which callback type to pass?
                        (
                            (preload_this_script) ? // depends on script-preloading
                                function () {
                                    registry_item.ready = true;
                                    for (var i = 0; i < registry_item.ready_listeners.length; i++) {
                                        registry_item.ready_listeners[i]();
                                    }
                                    registry_item.ready_listeners = [];
                                } :
                                function () {
                                    script_executed(registry_item);
                                }
                        ),
                        // signal if script-preloading should be used or not
                        preload_this_script
                    );
            }
            else {
                registry_item = registry_items[0];
                if (registry_item.finished) {
                    finished_cb();
                }
                else {
                    registry_item.finished_listeners.push(finished_cb);
                }
            }
        }

        // creates a closure for each separate chain spawned from this $LAB instance, to keep state cleanly separated between chains
        function create_chain() {
            var chainedAPI,
                chain_opts = merge_objs(global_defaults, {}),
                chain = [],
                exec_cursor = 0,
                scripts_currently_loading = false,
                group
            ;

            // called when a script has finished preloading
            function chain_script_ready(script_obj, exec_trigger) {
                /*!START_DEBUG*/
                if (chain_opts[_Debug]) log_msg("script preload finished: " + script_obj.real_src);
                /*!END_DEBUG*/
                script_obj.ready = true;
                script_obj.exec_trigger = exec_trigger;
                advance_exec_cursor(); // will only check for 'ready' scripts to be executed
            }

            // called when a script has finished executing
            function chain_script_executed(script_obj, chain_group) {
                /*!START_DEBUG*/
                if (chain_opts[_Debug]) log_msg("script execution finished: " + script_obj.real_src);
                /*!END_DEBUG*/
                script_obj.ready = script_obj.finished = true;
                script_obj.exec_trigger = null;
                // check if chain group is all finished
                for (var i = 0; i < chain_group.scripts.length; i++) {
                    if (!chain_group.scripts[i].finished) return;
                }
                // chain_group is all finished if we get this far
                chain_group.finished = true;
                advance_exec_cursor();
            }

            // main driver for executing each part of the chain
            function advance_exec_cursor() {
                while (exec_cursor < chain.length) {
                    if (is_func(chain[exec_cursor])) {
                        /*!START_DEBUG*/
                        if (chain_opts[_Debug]) log_msg("$LAB.wait() executing: " + chain[exec_cursor]);
                        /*!END_DEBUG*/
                        try {
                            chain[exec_cursor++]();
                        } catch (err) {
                            ///*!START_DEBUG*/if (chain_opts[_Debug]) log_error("$LAB.wait() error caught: ",err);/*!END_DEBUG*/
                            log_error("$LAB.wait() error caught: ", err);
                        }
                        continue;
                    }
                    else if (!chain[exec_cursor].finished) {
                        if (check_chain_group_scripts_ready(chain[exec_cursor])) continue;
                        break;
                    }
                    exec_cursor++;
                }
                // we've reached the end of the chain (so far)
                if (exec_cursor == chain.length) {
                    scripts_currently_loading = false;
                    group = false;
                }
            }

            // setup next chain script group
            function init_script_chain_group() {
                if (!group || !group.scripts) {
                    chain.push(group = {scripts: [], finished: true});
                }
            }

            // API for $LAB chains
            chainedAPI = {
                // start loading one or more scripts
                script: function () {
                    for (var i = 0; i < arguments.length; i++) {
                        (function (script_obj, script_list) {
                            var splice_args;

                            if (!is_array(script_obj)) {
                                script_list = [script_obj];
                            }
                            for (var j = 0; j < script_list.length; j++) {
                                init_script_chain_group();
                                script_obj = script_list[j];

                                if (is_func(script_obj)) script_obj = script_obj();
                                if (!script_obj) continue;
                                if (is_array(script_obj)) {
                                    // set up an array of arguments to pass to splice()
                                    splice_args = [].slice.call(script_obj); // first include the actual array elements we want to splice in
                                    splice_args.unshift(j, 1); // next, put the `index` and `howMany` parameters onto the beginning of the splice-arguments array
                                    [].splice.apply(script_list, splice_args); // use the splice-arguments array as arguments for splice()
                                    j--; // adjust `j` to account for the loop's subsequent `j++`, so that the next loop iteration uses the same `j` index value
                                    continue;
                                }
                                if (typeof script_obj == "string") script_obj = {src: script_obj};
                                script_obj = merge_objs(script_obj, {
                                    ready: false,
                                    ready_cb: chain_script_ready,
                                    finished: false,
                                    finished_cb: chain_script_executed
                                });
                                group.finished = false;
                                group.scripts.push(script_obj);

                                do_script(chain_opts, script_obj, group, (can_use_preloading && scripts_currently_loading));
                                scripts_currently_loading = true;

                                if (chain_opts[_AlwaysPreserveOrder]) chainedAPI.wait();
                            }
                        })(arguments[i], arguments[i]);
                    }
                    return chainedAPI;
                },
                // force LABjs to pause in execution at this point in the chain, until the execution thus far finishes, before proceeding
                wait: function () {
                    if (arguments.length > 0) {
                        for (var i = 0; i < arguments.length; i++) {
                            chain.push(arguments[i]);
                        }
                        group = chain[chain.length - 1];
                    }
                    else group = false;

                    advance_exec_cursor();

                    return chainedAPI;
                }
            };

            // the first chain link API (includes `setOptions` only this first time)
            return {
                script: chainedAPI.script,
                wait: chainedAPI.wait,
                setOptions: function (opts) {
                    merge_objs(opts, chain_opts);
                    return chainedAPI;
                }
            };
        }

        // API for each initial $LAB instance (before chaining starts)
        instanceAPI = {
            // main API functions
            setGlobalDefaults: function (opts) {
                merge_objs(opts, global_defaults);
                /*!START_DEBUG*/
                if (global_defaults[_Debug]) log_msg("browser info: " + JSON.stringify($browser));
                /*!END_DEBUG*/
                return instanceAPI;
            },
            setOptions: function () {
                return create_chain().setOptions.apply(null, arguments);
            },
            script: function () {
                return create_chain().script.apply(null, arguments);
            },
            wait: function () {
                return create_chain().wait.apply(null, arguments);
            },

            // built-in queuing for $LAB `script()` and `wait()` calls
            // useful for building up a chain programmatically across various script locations, and simulating
            // execution of the chain
            queueScript: function () {
                queue[queue.length] = {type: "script", args: [].slice.call(arguments)};
                return instanceAPI;
            },
            queueWait: function () {
                queue[queue.length] = {type: "wait", args: [].slice.call(arguments)};
                return instanceAPI;
            },
            runQueue: function () {
                var $L = instanceAPI, len = queue.length, i = len, val;
                for (; --i >= 0;) {
                    val = queue.shift();
                    $L = $L[val.type].apply(null, val.args);
                }
                return $L;
            },

            // rollback `[global].$LAB` to what it was before this file was loaded, the return this current instance of $LAB
            noConflict: function () {
                global.$LAB = _$LAB;
                return instanceAPI;
            },

            // create another clean instance of $LAB
            sandbox: function () {
                return create_sandbox();
            },
            //add by jac: 2016.5.17 调用服务器url, 返回结果
            //{src: "test1.txt", callback: onload, dataType: "text"}
            text: function (params) {
                if (typeof params == "string") params = {src: params};
                //Should we disable caching
                var real_src = canonical_uri(params.src, global_defaults[_BasePath]);
                real_src = real_src +
                    // append cache-bust param to URL?
                    (global_defaults[_CacheBust] ? ((/\?.*$/.test(params.src) ? "&_" : "?_") + ~~(Math.random() * 1E9) + "=") : "")
                ;
                //if(typeof params == "object") url  += (url.indexOf('?') != -1 ? '&' : '?') + this.urlEncode(params);
                var xmlHttp = new XMLHttpRequest();
                /*!START_DEBUG*/
                if (global_defaults[_Debug]) log_msg("start text load (xhr): " + real_src);
                /*!END_DEBUG*/
                xmlHttp.open("GET", real_src, params.callback != null);
                if (params.callback != null) {
                    xmlHttp.onreadystatechange = function () {
                        if (xmlHttp.readyState != 4)
                            return;
                        params.callback(xmlHttp.responseText);
                    }
                }
                // 发送数据
                xmlHttp.send();   //reqData
                // 返回text文档数据
                return params.callback == null ? xmlHttp.responseText : null;
            }
        };

        return instanceAPI;
    }

    // create the main instance of $LAB
    global.$LAB = create_sandbox();


    /* The following "hack" was suggested by Andrea Giammarchi and adapted from: http://webreflection.blogspot.com/2009/11/195-chars-to-help-lazy-loading.html
       NOTE: this hack only operates in FF and then only in versions where document.readyState is not present (FF < 3.6?).

       The hack essentially "patches" the **page** that LABjs is loaded onto so that it has a proper conforming document.readyState, so that if a script which does
       proper and safe dom-ready detection is loaded onto a page, after dom-ready has passed, it will still be able to detect this state, by inspecting the now hacked
       document.readyState property. The loaded script in question can then immediately trigger any queued code executions that were waiting for the DOM to be ready.
       For instance, jQuery 1.4+ has been patched to take advantage of document.readyState, which is enabled by this hack. But 1.3.2 and before are **not** safe or
       fixed by this hack, and should therefore **not** be lazy-loaded by script loader tools such as LABjs.
    */
    (function (addEvent, domLoaded, handler) {
        if (document.readyState == null && document[addEvent]) {
            document.readyState = "loading";
            document[addEvent](domLoaded, handler = function () {
                document.removeEventListener(domLoaded, handler, false);
                document.readyState = "complete";
            }, false);
        }
    })("addEventListener", "DOMContentLoaded");

})(this);
