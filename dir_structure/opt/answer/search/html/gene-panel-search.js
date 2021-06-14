Vue.component('gene-panel-search', {
    props: {
    },
    template: /*html*/`<div>
    <v-container grid-list-md text-xs-center fluid>
    <v-toolbar fixed app flat>
    <v-toolbar-title class="headline">
        Gene Panel Search
        <v-tooltip bottom>
        <v-btn class="primary--text" slot="activator" icon flat @click="helpVisible = !helpVisible">
        <v-icon>help</v-icon>
        </v-btn>
        <span>Toggle Help</span>
        </v-tooltip>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <div class="toolbar-image">
      <img class="toolbar-image pt-2 pb-2" alt="ngs logo"
        src="NGS_Lab_Color.png">
      <img class="toolbar-image" alt="utsw master logo"
        src="utsw-master-logo-lg.png">
    </div>
  </v-toolbar>
        <v-layout row wrap align-center>
        <v-fade-transition>
            <v-flex xs1 v-show="helpVisible" class="primary--text"><b>STEP 1:</b></v-flex>
            </v-fade-transition>
            <v-flex xs4>
                <v-select v-bind:items="selectPanelData" v-model="panelSelected" item-text="text" item-value="value" label="Select a Panel"
                    autocomplete @input="handleSelectPanelChanged"></v-select>
            </v-flex>
        </v-layout>
        <v-layout row wrap>

            <v-flex xs3>
                <v-card>
                <v-fade-transition>
                <div class="text-xs-left pa-2 primary--text" v-show="helpVisible"><span><b>STEP 2: </b>Enter a list of gene symbols or synonyms separated by a comma or a line return</span></div>
                </v-fade-transition>
                    <v-card-title primary-title>
                        <v-tooltip bottom>
                        <div slot="activator" class="subheading">Search for Genes (2 letters min.)</div>
                        <span>Enter a list of gene symbols or synonyms<br/>separated by a comma or a line return</span>
                        </v-tooltip>
                        </v-card-title>
                        <v-card-actions class="pl-2">
                        <v-checkbox @change="handleStrictChanged" hide-details label="Exact Match" v-model="isScrict"></v-checkbox>
                    </v-card-actions>
                    <v-card-text>
                        <v-text-field ref="genesTextField" auto autofocus auto-grow :disabled="!panelSelected" textarea label="Insert Gene List Here"
                            v-model="originalList" @input="handleGeneListChanged">
                        </v-text-field>
                    </v-card-text>

                </v-card>
            </v-flex>
            <!-- Add a vcard with genes parsed and the ones not in "Genes Not Found"
        highlighted in bold -->
            <v-flex xs2>
                <v-card style="height:100%">
                <v-fade-transition>
                <div class="text-xs-left pa-2 primary--text" v-show="helpVisible"> <span>This card shows your gene list parsed (removes extra commas, white space, etc.)</span></div>
                </v-fade-transition>
                    <v-card-title primary-title>
                    <v-tooltip bottom>
                        <div slot="activator" class="subheading">Parsed Search</div>
                        <span>This card shows your gene list parsed<br/>(removes extra commas, white space, etc.) </span>
                        </v-tooltip>
                        <v-spacer></v-spacer>
                        </v-card-title>
                        <v-card-actions>
                        <v-spacer>
                        </v-spacer>
                                <v-tooltip bottom>
                                    <v-btn :disabled="isItemListEmpty()" flat icon @click="saveItems" slot="activator">
                                        <v-icon>file_download</v-icon>
                                    </v-btn>
                                    <span>Export to Text File</span>
                                </v-tooltip>
                    </v-card-actions>
                    <v-list>
                        <span v-for="item in itemList" key="item">
                            <v-list-tile @click="">
                                <v-list-tile-content>
                                    <v-list-tile-title v-html="item" class="selectable"></v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            <v-divider></v-divider>
                        </span>
                    </v-list>
                </v-card>
            </v-flex>

            <v-flex xs5>
                <v-card style="height:100%">
                <v-fade-transition>
                <div class="text-xs-left pa-2 primary--text" v-show="helpVisible"><span>This card outputs the list of gene symbols (and synonyms) in the selected panel</span></div>
                </v-fade-transition>
                    <v-card-title primary-title>
                    <v-tooltip bottom>
                        <div slot="activator" class="subheading">Genes Found In Current Panel: {{ numGenesFound }}</div>
                        <span>This card outputs the list of gene symbols (and synonyms) in the selected panel</span>
                        </v-tooltip>
                        </v-card-title>
                    <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-tooltip bottom>
                                    <v-btn :disabled="isGeneFoundEmpty()" flat icon @click="saveFound" slot="activator">
                                        <v-icon>file_download</v-icon>
                                    </v-btn>
                                    <span>Export to Text File</span>
                                </v-tooltip>
                        </v-card-actions>
                    <v-list>
                        <span v-for="item in foundListItems" key="item">
                            <v-list-tile @click="">
                                <v-list-tile-content>
                                    <v-list-tile-title v-html="item" class="selectable"></v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            <v-divider></v-divider>
                        </span>
                    </v-list>

                </v-card>
            </v-flex>

            <v-flex xs2>
                <v-card style="height:100%">
                <v-fade-transition>
                <div class="text-xs-left pa-2 primary--text" v-show="helpVisible"><span>This card outputs the list of genes that could not be found in the current panel</span></div>
                </v-fade-transition>
                    <v-card-title primary-title>
                        <div class="subheading">Genes Not Found</div>
                    </v-card-title>
                    <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-tooltip bottom>
                                <v-btn :disabled="isGeneNotFoundEmpty()" flat icon @click="saveNotFound" slot="activator">
                                    <v-icon>file_download</v-icon>
                                </v-btn>
                                <span>Export to Text File</span>
                            </v-tooltip>
                    </v-card-actions>
                    <v-list>
                        <span v-for="item in notFoundListItems" key="item">
                            <v-list-tile @click="">
                                <v-list-tile-content>
                                    <v-list-tile-title v-html="item" class="selectable"></v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            <v-divider></v-divider>
                        </span>
                    </v-list>
                </v-card>
            </v-flex>

        </v-layout>
    </v-container>
</div>`, data() {
        return {
            originalList: "",
            foundListItems: [],
            notFoundListItems: [],
            geneSymbolList: [],
            panelSelected: "Comprehensive Pan Cancer NGS", //default panel
            selectPanelData: selectPanelData,
            textToCopy: "",
            isScrict: true,
            numGenesFound: 0,
            itemList: [],
            helpVisible: false
        }
    },
    methods: {
        handleGeneListChanged(input) {
            this.foundListFormatted = "";
            this.notFoundListFormatted = "";
            var foundList = new Map(); // use map to avoid duplicates in objects
            var notFoundList = new Set(); // using strings here, so Set works fine
            this.foundListItems = [];
            this.notFoundListItems = [];
            this.itemList = [];

            if (input && input != "" && this.panelSelected != "") {
                var list = input.split(/[^A-Za-z0-9_-]+/g);
                for (var i = 0; i < list.length; i++) {
                    var gene = list[i].trim().toUpperCase();
                    var itemStyled = gene; //used by itemList to highlight genes found
                    if (gene.length >= 2) {
                        var wasFound = false;
                        for (var [key, value] of panels[this.panelSelected].geneSynonyms.entries()) {
                            if (this.isScrict) {
                                var synonyms = key.split(",");
                                var indexLookup = synonyms.indexOf(gene);
                                //insures that an exact match is found
                                if (indexLookup > -1 && synonyms[indexLookup] == gene) {
                                    foundList.set(key, { value: value, highlight: gene });
                                    wasFound = true;
                                    
                                }
                            }
                            else if (key.indexOf(gene) > -1) {
                                    foundList.set(key, { value: value, highlight: gene });
                                    wasFound = true;
                            }
                        }
                        if (wasFound) {
                            itemStyled = this.highlightItem(gene);
                        }
                        if (!wasFound) {
                            notFoundList.add(gene);
                        }
                        //add it to the list of items from the search box
                        //only if unique
                        var unique = true;
                        for (var j = 0; j < this.itemList.length; j++) {
                            var item = this.removeHighlightFromString(this.itemList[j]);
                            if (item == gene) {
                                unique = false;
                                break;
                            }
                            
                        }
                        if (unique) {
                            this.itemList.push(itemStyled);
                        }
                    }
                }
                for (var [key, value] of foundList.entries()) {
                    var entry = null;
                    var symbol = value.value;
                    var highlight = value.highlight;
                    if (this.isScrict) {
                        var synonyms = panels[this.panelSelected].geneSymbols[symbol].split(",");
                        var indexLookup = synonyms.indexOf(highlight);
                        //insures that an exact match is found
                        var synonymsString = null;
                        var symbolString = symbol;
                        if (indexLookup > -1 && synonyms[indexLookup] == highlight) {
                            synonyms[indexLookup] = this.highlightItem(highlight);
                        }
                        synonymsString = " (" + synonyms.join(",") + ")";
                        if (symbol == highlight) {
                            symbolString = this.highlightItem(symbol);
                        }
                        entry = symbolString + synonymsString;
                    }
                    else {
                        entry = symbol + " (" + panels[this.panelSelected].geneSymbols[symbol] + ")";
                        entry = entry.replace(highlight, this.highlightItem(highlight));

                    }
                    this.foundListItems.push(entry);
                }
                notFoundList.forEach(gene => {
                    this.notFoundListItems.push(gene);
                });
            }
            this.numGenesFound = this.foundListItems.length;
        },
        handleSelectPanelChanged(input) {
            //clear previous data
            this.$refs.genesTextField.reset();
            //focus the gene text field when a panel has been selected
            this.$nextTick(this.$refs.genesTextField.focus);
        },
        saveFound() {
            this.save(this.foundListItems);
        },
        saveNotFound() {
            this.save(this.notFoundListItems);
        },
        saveItems() {
            //Add a column in the csv file to show which item was found and which wasn't
            var list = [];
            for (var i = 0; i < this.itemList.length; i++) {
                var item = this.itemList[i];
                if (item.indexOf("<strong>") > -1) {
                    list.push(item + ", found");
                }
                else {
                    list.push(item + ", not found");
                }
            }
            this.save(list);
        },
        save(list) {
            this.textToCopy = this.removeHighlightFromItems(list);
            var hiddenElement = document.createElement('a');
            hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(this.textToCopy);
            //hiddenElement.target = '_blank';
            hiddenElement.download = this.panelSelected + '_search_results.txt';
            document.body.appendChild(hiddenElement);
            hiddenElement.click();
            document.body.removeChild(hiddenElement);
        },
        isGeneFoundEmpty() {
            return this.foundListItems.length == 0;
        },
        isItemListEmpty() {
            return this.itemList.length == 0;
        },
        isGeneNotFoundEmpty() {
            return this.notFoundListItems.length == 0;
        },
        handleStrictChanged() {
            this.handleGeneListChanged(this.$refs.genesTextField.lazyValue);
        },
        removeHighlightFromItems(list) {
            return this.removeHighlightFromString(list.join("\n"));
        },
        removeHighlightFromString(string) {
            return string.replace(/<strong>/g, '').replace(/<\/strong>/g, '');
        },
        highlightItem(item) {
            return "<strong>" + item + "</strong>"
        }
    },
    computed: {

    },
    created: function () {

    },
    destroyed: function () {
    },
    mounted: function () {

    }



});

