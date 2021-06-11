Vue.component('clinical-tests-edit', {
  template:
    /*html*/`<div>

  <v-snackbar :timeout="4000" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

  <!-- edit gene set dialog -->
  <v-dialog v-model="editClinicalTestDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">
          {{ editAdd }} Clinical Test: {{ currentEditClinicalTestName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
          <v-btn icon @click="cancelEditsClinicalTest()" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Cancel</span>
        </v-tooltip>
      </v-toolbar>
      <v-card-text :style="getDialogMaxHeight()">
        <v-container grid-list-md fluid class="pt-2">
          <v-layout row wrap>
            <v-flex xs12 lg6>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Clinical Test Details:</div>
                </v-card-title>
                <v-card-text>
                  <v-text-field v-model="editName" label="Name"></v-text-field>
                  <v-text-field v-model="editUrl" label="Link to More Info"></v-text-field>
                  <v-textarea v-model="editText" label="Disclaimers" auto-grow></v-textarea>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="saveEditsClinicalTest()" :disabled="saveClinicalTestDisabled">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelEditsClinicalTest()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Create new Gene sets -->
  <data-table ref="clinicalTestsTable" :fixed="false" :fetch-on-created="true" table-title="Clinical Tests" :initial-sort="'name'" no-data-text="No Data"
    data-url="./getAllClinicalTests" class="mt-2">
    <v-fade-transition slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="createClinicalTest" slot="activator">
          <v-icon dark>playlist_add</v-icon>
        </v-btn>
        <span>Create a New Clinical Test</span>
      </v-tooltip>
    </v-fade-transition>
    <v-list-tile avatar @click="createClinicalTest" slot="action1MenuItem">
      <v-list-tile-avatar>
        <v-icon>playlist_add</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title>Create a New Clinical Test</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </data-table>

</div>`,
  data() {
    return {
      editUserDialogVisible: false,
      currentEditUrl: "",
      editAdd: "Add",
      snackBarVisible: false,
      snackBarMessage: "",
      editClinicalTestDialogVisible: false,
      currentEditClinicalTestName: "",
      currentEditClinicalTestId: null,
      editName: "",
      saveClinicalTestDisabled: false,
      editUrl: "",
      editText: "",
    }
  },
  methods: {
    getDialogMaxHeight() {
      var height = window.innerHeight - 120;
      return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
    },
    handleDialogs(response, callback) {
      if (response.isXss) {
        bus.$emit("xss-error", [null, response.reason]);
      }
      else if (response.isLogin) {
        bus.$emit("login-needed", [null, callback])
      }
      else if (response.success === false) {
        bus.$emit("some-error", [null, response.message]);
      }
    },

    createClinicalTest() {
      this.editAdd = "Add";
      this.currentEditClinicalTestId = null;
      this.editUrl = "";
      this.editText = "";
      this.editName = "";
      this.editClinicalTestDialogVisible = true;
    },
    editClinicalTest(test) {
      if (this.$refs.clinicalTestsTable) {
        this.editAdd = "Edit";
        var clinicalTest = this.$refs.clinicalTestsTable.items.filter(item => item.testId == test.testId)[0];
        this.currentEditClinicalTestId = clinicalTest.testId;
        this.currentEditName = clinicalTest.test;
        this.editName = clinicalTest.name;
        this.editText = clinicalTest.lines.join("\n\n");
        this.editUrl = clinicalTest.url;
        this.editClinicalTestDialogVisible = true;
      }
    },
    cancelEditsClinicalTest() {
      this.editClinicalTestDialogVisible = false;
    },
    saveEditsClinicalTest() {
      this.snackBarMessage = this.currentEditClinicalTestId ? 'Clinical Test saved successfully' : 'Clinical Test Added successfully';
      this.saveClinicalTestDisabled = true;
      axios({
        method: 'post',
        url: "./saveClinicalTest",
        params: {
          clinicalTestId: this.currentEditClinicalTestId,
        },
        data: {
          name: this.editName,
          textConcat: this.editText,
          url: this.editUrl,
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            if (this.$refs.clinicalTestsTable) {
              this.$refs.clinicalTestsTable.getAjaxData();
              this.snackBarVisible = true;
              this.editClinicalTestDialogVisible = false;
            }
          }
          else {
            this.handleDialogs(response.data, this.saveEditsClinicalTest);
          }
          this.saveClinicalTestDisabled = false;
        })
        .catch(error => {
          alert(error);
          this.saveClinicalTestDisabled = false;
        });
    },
  },
  mounted: function () {
  },
  destroyed: function () {
    // bus.$off('editReportGroup');
    // bus.$off('deleteReportGroup');
  },
  beforeDestroy() {
    bus.$off('editClinicalTest', this.editClinicalTest);
  },
  created: function () {
    bus.$on('editClinicalTest', this.editClinicalTest);
  },
  computed: {
  },
  watch: {
  }
});

