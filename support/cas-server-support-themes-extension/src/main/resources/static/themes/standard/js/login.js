(function(){
    /**
     * 初始化 Tabs
     */
    function initTabs() {
        if (!document.querySelector("#loginTabBar")) {
            return
        }
        let loginTabBar  = new mdc.tabBar.MDCTabBar(document.querySelector("#loginTabBar"));

        const tabs = document.querySelectorAll('.mdc-tab');
        const panels = document.querySelectorAll('.tab-panel');

        loginTabBar.listen('MDCTabBar:activated', (event) => {
            const activeTabIndex = event.detail.index;
            const activeTab = tabs[activeTabIndex];
            const targetTabId = activeTab.dataset.tab;

            panels.forEach(panel => panel.classList.remove('active'));
            document.querySelector(`[data-panel="${targetTabId}"]`).classList.add('active');
        });
    }

}())