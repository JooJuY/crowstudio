import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

import { createTeam } from "../../redux/teamSlice";

import Header from "../../components/Header";

const initialInputState = { teamName: "", projectName: "", templateName: "" };
const initialErrorState = {
  teamNameErrMsg: "",
  projectNameErrMsg: "",
  templateNameErrMsg: "",
};

const TeamCreate = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [inputs, setInputs] = useState(initialInputState);
  const [errorMsgs, setErrorMsgs] = useState(initialErrorState);
  const { teamName } = inputs;
  // const { teamName, projectName, templateName } = inputs;
  const { teamNameErrMsg } = errorMsgs;
  // const { teamNameErrMsg, projectNameErrMsg, templateNameErrMsg } = errorMsgs;

  //
  const inputChangeHandler = (e) => {
    if (e.target.name === "teamName") {
      setInputs((prev) => {
        return { ...prev, teamName: e.target.value };
      });
    }
    // else if (e.target.name === "projectName") {
    //   setInputs((prev) => {
    //     return { ...prev, projectName: e.target.value };
    //   });
    // } else if (e.target.name === "templateName") {
    //   setInputs((prev) => {
    //     return { ...prev, templateName: e.target.value };
    //   });
    // }
  };

  //
  const submitHandler = (e) => {
    e.preventDefault();

    let isInvalid = false;
    setErrorMsgs(initialErrorState);
    if (teamName.trim().length === 0) {
      setErrorMsgs((prev) => {
        return { ...prev, teamNameErrMsg: "팀 이름을 입력하세요" };
      });
      isInvalid = true;
    }
    if (teamName.trim() === "400" || teamName.trim() === "403") {
      setErrorMsgs((prev) => {
        return { ...prev, teamNameErrMsg: "사용할 수 없는 팀 이름입니다" };
      });
      isInvalid = true;
    }
    if (isInvalid) {
      return;
    }

    const teamNameData = JSON.stringify({ teamName });
    setErrorMsgs(initialErrorState);
    dispatch(createTeam(teamNameData))
      .unwrap()
      .then(() => {
        alert("팀 생성 완료");
        navigate("/teams", { replace: true });
      })
      .catch((errorStatusCode) => {
        if (errorStatusCode === 409) {
          alert("해당 이름으로 생성된 팀이 있습니다");
        } else {
          alert("비상!!");
        }
      });
  };

  const clickTeamListHandler = () => navigate("/teams");

  return (
    <div>
      <Header />
      <div className="p-8 flex flex-col justify-center items-center border border-primary_-2_dark rounded-md">
        <form
          method="post"
          onSubmit={submitHandler}
          className="flex flex-col items-center"
        >
          {/* Team Name */}
          <div className="w-80 mb-1">
            <label htmlFor="teamName" className="">
              팀 이름
            </label>
            <input
              type="teamName"
              id="teamName"
              name="teamName"
              className="mt-1 w-full text-component_dark py-2 px-3 placeholder:text-gray-300 placeholder:text-sm rounded-md transition"
              placeholder="팀 이름을 입력하세요"
              required
              value={teamName}
              onChange={inputChangeHandler}
            />
            <div className="h-6 mt-1 ml-3 mb-0.5 text-sm text-point_pink">
              {teamNameErrMsg}
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-80 text-lg font-bold text-component_dark bg-point_light_yellow hover:bg-point_yellow py-2 px-6 rounded-md transition mb-4"
            onClick={submitHandler}
          >
            팀 생성
          </button>
        </form>
        <button
          onClick={clickTeamListHandler}
          className="w-80 px-10 py-2 text-primary_dark bg-component_item_bg_dark border border-primary_-2_dark rounded-md"
        >
          팀 목록
        </button>
      </div>
    </div>
  );
};

export default TeamCreate;