<template>
  <transition name="fade">
    <div
      v-if="isShow"
      @click="handleClickOverlay"
      class="vc-overlay"
      id="vueConfirm"
    >
      <transition name="zoom">
        <div v-if="isShow" ref="vueConfirmDialog" class="vc-container">
          <span class="vc-text-grid">
            <h4 v-if="dialog.title" class="vc-title" v-html="dialog.title"></h4>
            <p
              v-if="dialog.message"
              class="vc-text"
              v-html="dialog.message"
            ></p>
            <span v-if="dialog.auth">
              <input
                v-focus
                v-model="info.email"
                @keyup.13="(e) => handleClickButton(e, true)"
                class="vc-input"
                type="email"
                name="vc-password"
                placeholder="이메일"
                autocomplete="off"
              />
            </span>
            <p class="text-h5 red-text" v-if="error !== ''">
              {{ error }}
            </p>
          </span>
          <div
            class="vc-btn-grid"
            :class="{ isMono: !dialog.button.no || !dialog.button.yes }"
          >
            <button
              v-if="dialog.button.no"
              @click.stop="(e) => handleClickButton(e, false)"
              class="vc-btn left"
              type="button"
            >
              {{ dialog.button.no }}
            </button>

            <button
              v-if="dialog.button.yes"
              @click.stop="(e) => handleClickButton(e, true)"
              class="vc-btn"
              type="button"
              @click="select_vote()"
            >
              {{ dialog.button.yes }}
            </button>
          </div>
        </div>
      </transition>
    </div>
  </transition>
</template>

<script>
import Vue from "vue";
import { events } from "../../components/common/events";

import {
  participateVote,
  nonparticipateVote,
  changVoteStatus,
  voteDelete,
} from "@/api/community.js";
import { resetPassword } from "@/api/user";
import { deleteUserInfo } from "@/api/mypage";
import { mapMutations, mapState } from "vuex";

Vue.directive("focus", {
  inserted: function (el) {
    el.focus();
  },
});
const Component = {
  name: "VueConfirmDialog",
  props: {
    data: Object,
    select: String,
    voteId: Number,
    ballotId: Number,
  },

  data() {
    return {
      info: {
        email: "",
      },
      isShow: this.data.isShow,
      error: "",
      password: null,
      dialog: {
        auth: this.data.dialog,
        title: this.data.title,
        message: this.data.message,
        button: {
          no: this.data.no,
          yes: this.data.yes,
        },
      },
      params: {},
    };
  },
  computed: {
    ...mapState("userStore", ["accessToken"]),
  },
  methods: {
    ...mapMutations("communityStore", ["SET_SELECT"]),
    ...mapMutations("userStore", ["SET_INIT"]),
    resetState() {
      this.password = null;
      this.dialog = {
        auth: false,
        title: "",
        message: "",
        button: {},
        callback: () => {},
      };
    },

    select_vote() {
      //api사용
      console.log("안녕?:" + this.data.mode);
      // var token =
      //   "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwiYXVkIjoi7LmY7YKo65-s67KEIiwiZXhwIjoxNjY0OTU4NDQwfQ.zevo64PasEuPNiBQAaI0Fs-ZKZjlCb7catVf4AQnfTd-0vmJqbRU_rUQrJUxgGip";

      if (this.data.mode == "1") {
        //투표하기

        var params = {
          voteId: this.voteId,
          choice: this.select,
        };

        participateVote(this.accessToken, params, ({ data }) => {
          console.log(this.accessToken);
          console.log(data);
          this.info = data;
          // this.$router.go();
          this.$router.go();
        });
      } else if (this.data.mode == "2") {
        //투표취소
        console.log(this.ballotId);
        nonparticipateVote(this.accessToken, this.ballotId, ({ data }) => {
          console.log("취소:", data);
          this.info = data;
          this.$router.go();
        });
      } else if (this.data.mode === "3") {
        // 회원가입 완료
        this.$router.push({ name: "userLogin", path: "/profile/login" });
      } else if (this.data.mode === "4") {
        // 비밀번호 재 설정
        this.sendEmail();
      } else if (this.data.mode === "5") {
        // 회원 탈퇴
        this.deleteUser();
      } else if (this.data.mode == "6") {
        //투표 마감
        let params = { status: true };
        changVoteStatus(this.accessToken, this.voteId, params, ({ data }) => {
          console.log("마감:" + data);
          this.$router.go();
        });
      } else if (this.data.mode === "7") {
        this.deleteVote();
      }
    },
    handleClickButton({ target }, confirm) {
      this.data.isShow = false;
      // this.data.dialog = false;
      if (target.id == "vueConfirm") return;

      if (confirm && this.dialog.auth && !this.password) return;

      // this.$emit("change_show", (this.show1 = false), (this.show2 = false));

      // callback
      if (this.params.callback) {
        this.params.callback(confirm, this.password);
      }
    },
    handleClickOverlay({ target }) {
      if (target.id == "vueConfirm") {
        this.data.isShow = false;
        // callback

        if (this.params.callback) {
          this.params.callback(false, this.password);
        }
      }
    },
    handleKeyUp({ keyCode }) {
      if (keyCode == 27) {
        this.handleClickOverlay({ target: { id: "vueConfirm" } });
      }
      if (keyCode == 13) {
        this.handleClickButton({ target: { id: "" } }, true);
      }
    },
    open(params) {
      this.resetState();
      this.params = params;
      this.isShow = true;
      // set params to dialog state
      Object.entries(params).forEach((param) => {
        if (typeof param[1] == typeof this.dialog[param[0]]) {
          this.dialog[param[0]] = param[1];
        }
      });
    },
    sendEmail() {
      this.error = "이메일을 확인 해 주세요!!(5초 후 자동으로 종료됩니다.)";
      resetPassword(
        this.info,
        (response) => {
          this.data.isShow = false;
          console.log(response.data);
          console.log("이메일 보냄");
        },
        (error) => {
          console.log(error);
          console.log("이메일 틀림");
          this.error = "등록된 이메일이 없습니다.";
        }
      );
    },
    deleteUser() {
      deleteUserInfo(
        this.accessToken,
        this.userId,
        (response) => {
          console.log(response.data);
          this.data.isShow = false;
          this.SET_INIT();
          this.$store.state.started = 0;
          this.$router.push({ name: "home", path: "/" });
        },
        (error) => {
          console.log(error);
        }
      );
    },
    deleteVote() {
      voteDelete(
        this.accessToken,
        this.data.voteId,
        (response) => {
          console.log(response.data);
          this.$router.push({ name: "home", path: "/" });
        },
        (error) => {
          console.log(error);
        }
      );
    },
  },

  mounted() {
    if (!document) return;
    events.$on("open", this.open);
    events.$on("close", () => {
      this.handleClickOverlay({ target: { id: "vueConfirm" } });
    });
    // document.addEventListener('keyup', this.handleKeyUp)
  },
  beforeDestroy() {
    // document.removeEventListener('keyup', this.handleKeyUp)
  },
};
export default Component;
</script>

<style>
:root {
  --title-color: black;
  --message-color: black;
  --overlay-background-color: #0000004a;
  --container-box-shadow: #0000004a 0px 3px 8px 0px;
  --base-background-color: #ffffff;
  --button-color: #4083ff;
  --button-background-color: #ffffff;
  --button-border-color: #e0e0e0;
  --button-background-color-disabled: #f5f5f5;
  --button-background-color-hover: #f5f5f5;
  --button-box-shadow-active: inset 0 2px 0px 0px #00000014;
  --input-background-color: #ebebeb;
  --input-background-color-hover: #dfdfdf;
  --font-size-m: 16px;
  --font-size-s: 14px;
  --font-weight-black: 900;
  --font-weight-bold: 700;
  --font-weight-medium: 500;
  --font-weight-normal: 400;
  --font-weight-light: 300;
}
/**
* Dialog
*/
.vc-overlay *,
.vc-overlay *:before,
.vc-overlay *:after {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  text-decoration: none;
  -webkit-touch-callout: none;
  -moz-osx-font-smoothing: grayscale;
  margin: 0;
  padding: 0;
}
.vc-title {
  color: var(--title-color);
  /* padding: 0 1rem; */
  width: 100%;
  font-weight: var(--font-weight-black);
  text-align: center;
  font-size: var(--font-size-m);
  line-height: initial;
  margin-bottom: 5px;
}
.vc-text {
  color: var(--message-color);
  /* padding: 0 1rem; */
  width: 100%;
  font-weight: var(--font-weight-medium);
  text-align: center;
  font-size: var(--font-size-s);
  line-height: initial;
  margin-top: 20px;
}
.vc-overlay {
  background-color: var(--overlay-background-color);
  width: 100%;
  height: 100%;
  transition: all 0.1s ease-in;
  left: 0;
  top: 0;
  z-index: 999999999999;
  position: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  align-content: baseline;
}
.vc-container {
  background-color: var(--base-background-color);
  border-radius: 1rem;
  width: 286px;
  height: auto;
  display: grid;
  grid-template-rows: 1fr max-content;
  box-shadow: var(--container-box-shadow);
}
.vc-text-grid {
  padding: 1rem;
}
.vc-btn-grid {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  border-radius: 0 0 1rem 1rem;
  overflow: hidden;
}
.vc-btn-grid.isMono {
  grid-template-columns: 1fr;
}
.vc-btn {
  border-radius: 0 0 1rem 0;
  color: var(--button-color);
  background-color: var(--button-background-color);
  border: 0;
  font-size: 1rem;
  border-top: 1px solid var(--button-border-color);
  cursor: pointer;
  font-weight: var(--font-weight-bold);
  outline: none;
  min-height: 50px;
}
.vc-btn:hover {
  background-color: var(--button-background-color-hover);
}
.vc-btn:disabled {
  background-color: var(--button-background-color-disabled);
}
.vc-btn:active {
  box-shadow: var(--button-box-shadow-active);
}
.vc-btn.left {
  border-radius: 0;
  border-right: 1px solid var(--button-border-color);
}
.vc-input[type="email"] {
  width: 100%;
  outline: none;
  border-radius: 8px;
  height: 35px;
  border: 0;
  margin: 5px 0;
  background-color: var(--input-background-color);
  padding: 0 0.5rem;
  font-size: var(--font-size-m);
  transition: 0.21s ease;
  margin-top: 30px;
}
.vc-input[type="email"]:hover,
.vc-input[type="email"]:focus {
  background-color: var(--input-background-color-hover);
}
/**
* Transition
*/
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.21s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
.zoom-enter-active,
.zoom-leave-active {
  animation-duration: 0.21s;
  animation-fill-mode: both;
  animation-name: zoom;
}
.zoom-leave-active {
  animation-direction: reverse;
}
@keyframes zoom {
  from {
    opacity: 0;
    transform: scale3d(1.1, 1.1, 1.1);
  }
  100% {
    opacity: 1;
    transform: scale3d(1, 1, 1);
  }
}
</style>
Footer © 2022 GitHub, Inc. Footer navigation Terms Privacy Security Status
